package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.response.rush.RushEventListResponseDto;
import JGS.CasperEvent.domain.event.dto.response.rush.RushEventOptionResponseDto;
import JGS.CasperEvent.domain.event.dto.response.rush.RushEventResponseDto;
import JGS.CasperEvent.domain.event.dto.response.rush.RushEventResultResponseDto;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.entity.event.RushOption;
import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushOptionRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.RushParticipantsRepository;
import JGS.CasperEvent.domain.event.service.redisService.RushEventRedisService;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.enums.Position;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.util.RepositoryErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RushEventService {
    private final RushEventRepository rushEventRepository;
    private final RushParticipantsRepository rushParticipantsRepository;
    private final RushOptionRepository rushOptionRepository;
    private final EventCacheService eventCacheService;
    private final RushEventRedisService rushEventRedisService;

    @Transactional
    public RushEventListResponseDto getAllRushEvents() {
        LocalDate today = LocalDate.now();

        // 오늘의 선착순 이벤트 꺼내오기
        RushEventResponseDto todayEvent = eventCacheService.getTodayEvent(today);

        // 모든 이벤트 꺼내오기
        List<MainRushEventResponseDto> mainRushEventDtoList = eventCacheService.getAllRushEvent();

        // 선착순 이벤트 전체 시작 날짜와 종료 날짜 구하기
        List<LocalDate> dates = mainRushEventDtoList.stream().map(rushEvent -> rushEvent.getStartDateTime().toLocalDate()).sorted().toList();

        LocalDate totalStartDate = dates.get(0);
        LocalDate totalEndDate = dates.get(dates.size() - 1);

        // 전체 이벤트 기간 구하기
        long activePeriod = totalStartDate.until(totalEndDate).getDays() + 1;

        // RushEvent를 DTO로 전환
        List<RushEventResponseDto> mainRushEventDtoList = rushEventList.stream()
                .map(RushEventResponseDto::withMain)
                .toList();

        // DTO 리스트와 서버 시간을 담은 RushEventListAndServerTimeResponse 객체 생성 후 반환
        return new RushEventListResponseDto(
                mainRushEventDtoList,
                LocalDateTime.now(),
                todayEvent.getRushEventId(),
                totalStartDate,
                totalEndDate,
                activePeriod
        );
    }

    // 응모 여부 조회
    public boolean isExists(String userId) {
        LocalDate today = LocalDate.now();
        Long todayEventId = eventCacheService.getTodayEvent(today).getRushEventId();
        return rushParticipantsRepository.existsByRushEvent_RushEventIdAndBaseUser_PhoneNumber(todayEventId, userId);
    }

    @Transactional
    public void apply(BaseUser user, int optionId) {
        LocalDate today = LocalDate.now();
        Long todayEventId = eventCacheService.getTodayEvent(today).getRushEventId();

        // 이미 응모한 회원인지 검증
        if (rushParticipantsRepository.existsByRushEvent_RushEventIdAndBaseUser_PhoneNumber(todayEventId, user.getPhoneNumber())) {
            throw new CustomException("이미 응모한 회원입니다.", CustomErrorCode.CONFLICT);
        }

        // eventId 를 이용하여 rushEvent 를 꺼냄
        RushEvent rushEvent = RepositoryErrorHandler.findByIdOrElseThrow(rushEventRepository, todayEventId, CustomErrorCode.NO_RUSH_EVENT);

        // redis incr 호출
//        rushEventRedisService.incrementOptionCount(todayEventId, optionId);


        // 새로운 RushParticipants 를 생성하여 DB 에 저장
        RushParticipants rushParticipants = new RushParticipants(user, rushEvent, optionId);
        rushParticipantsRepository.save(rushParticipants);
    }

    // 진행중인 게임의 응모 비율 반환
    public JGS.CasperEvent.domain.event.dto.response.rush.RushEventResultResponseDto getRushEventRate(BaseUser user) {
        LocalDate today = LocalDate.now();
        Long todayEventId = eventCacheService.getTodayEvent(today).getRushEventId();
        Optional<Integer> optionId = rushParticipantsRepository.getOptionIdByUserId(user.getPhoneNumber());

        long leftOptionCount = rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(todayEventId, 1);
        long rightOptionCount = rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(todayEventId, 2);

        // redis 에 캐싱 값 가져옴
//        long leftOptionCount = rushEventRedisService.getOptionCount(todayEventId, 1);
//        long rightOptionCount = rushEventRedisService.getOptionCount(todayEventId, 2);

        return JGS.CasperEvent.domain.event.dto.response.rush.RushEventResultResponseDto.of(
                optionId.orElseThrow(() -> new CustomException("유저가 응모한 선택지가 존재하지 않습니다.", CustomErrorCode.USER_NOT_FOUND)),
                leftOptionCount, rightOptionCount);
    }

    // 이벤트 결과를 반환
    // 응모하지 않은 유저가 요청하는 경우가 존재 -> 응모 비율만 반환하도록 수정
    @Transactional
    public JGS.CasperEvent.domain.event.dto.response.rush.RushEventResultResponseDto getRushEventResult(BaseUser user) {
        LocalDate today = LocalDate.now();
        RushEventResponseDto todayRushEvent = eventCacheService.getTodayEvent(today);
        Long todayEventId = todayRushEvent.getRushEventId();

        // 최종 선택 비율을 조회
        // TODO: 레디스에 캐시
        long leftOption = rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(todayEventId, 1);
        long rightOption = rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(todayEventId, 2);

        Optional<Integer> optionIdOptional = rushParticipantsRepository.getOptionIdByUserId(user.getPhoneNumber());
        if (optionIdOptional.isEmpty()) {
            return JGS.CasperEvent.domain.event.dto.response.rush.RushEventResultResponseDto.withDetail(
                    null,
                    leftOption,
                    rightOption,
                    null,
                    null,
                    null
            );
        }

        // optionId 를 꺼냄
        int optionId = optionIdOptional.get();

        // 동점인 경우
        if (leftOption == rightOption) {
            // 전체 참여자에서 등수 계산하기
            long rank = rushParticipantsRepository.findUserRankByEventIdAndUserId(todayRushEvent.getRushEventId(), user.getPhoneNumber());

            // 각 옵션 선택지를 더하여 전체 참여자 수 구하기
            long totalParticipants = leftOption + rightOption;

            // 당첨 여부
            boolean isWinner = rank <= todayRushEvent.getWinnerCount();

            return JGS.CasperEvent.domain.event.dto.response.rush.RushEventResultResponseDto.withDetail(optionId, leftOption, rightOption, rank, totalParticipants, isWinner);
        }

        long totalParticipants = (optionId == 1 ? leftOption : rightOption);

        // eventId, userId, optionId 를 이용하여 해당 유저가 응모한 선택지에서 등수를 가져옴
        long rank = rushParticipantsRepository.findUserRankByEventIdAndUserIdAndOptionId(todayRushEvent.getRushEventId(), user.getPhoneNumber(), optionId);

        // 해당 유저가 선택한 옵션이 패배한 경우
        if ((optionId == 1 && leftOption < rightOption) || (optionId == 2 && leftOption > rightOption)) {

            return JGS.CasperEvent.domain.event.dto.response.rush.RushEventResultResponseDto.withDetail(optionId, leftOption, rightOption, rank, totalParticipants, false);
        }

        // 당첨 여부
        boolean isWinner = rank <= todayRushEvent.getWinnerCount();

        return  RushEventResultResponseDto.withDetail(optionId, leftOption, rightOption, rank, totalParticipants, isWinner);
    }

    @Transactional
    public void setRushEvents() {
        // 테이블 초기화
        rushParticipantsRepository.deleteAllInBatch();
        rushOptionRepository.deleteAllInBatch();
        rushEventRepository.deleteAllInBatch();

        // 오늘의 날짜를 기준으로 시간 설정
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(2).plusSeconds(20).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusSeconds(20);

        // 각 이벤트에 대응하는 텍스트들
        String[][] texts = {
                {"첫 차는 저렴해야 한다", "가성비 좋게 저렴한 차로 시작해서 차근히 업그레이드하고 싶어", "가성비 좋은 도심형 전기차", "캐스퍼 일렉트릭은 전기차 평균보다 30% 저렴해요 첫 차로 캐스퍼 일렉트릭 어떤가요?", "첫 차는 안전해야 한다", "처음 사는 차인 만큼 안전한 차를 사서 오래 타고 싶어", "가성비 좋은 도심형 전기차", "캐스퍼는 작고 귀엽기만 하다고 생각했나요? 이젠 현대 스마트센스 안전옵션까지 지원해요"},
                {"온라인 쇼핑이 편해서 좋다", "편한게 최고야! 집에서 인터넷으로 쇼핑할래", "현대 유일 온라인 예약", "차 살 때도 온라인 쇼핑을! 오직 온라인에서만 구매할 수 있는 캐스퍼 일렉트릭", "오프라인 쇼핑이 확실해서 좋다", "살 거면 제대로 사야지! 직접 보고 나서 판단할래", "캐스퍼 스튜디오 송파", "캐스퍼 일렉트릭을 원하는 시간에 직접 만나볼 수 있는 무인 전시 스튜디오"},
                {"텐트 치고 캠핑하기", "캠핑은 텐트가 근본이지!", "V2L로 캠핑 준비 끝", "캠핑장 전기 눈치싸움은 이제 그만! 차에 직접 220V 전원을 연결할 수 있어요", "차 안에서 차박하기", "가벼운 짐으로 차에서 잠드는 낭만이 좋아", "풀 폴딩으로 공간활용", "모든 시트가 완전히 접혀서 나만의 작은 방으로 만들 수 있어요"},
                {"평생 주차 무료로 하기", "요즘 주차장은 너무 비싸 주차비 걱정은 그만 하고 싶어", "전기차는 주차비 혜택 받아요", "공영주차장 50% 할인 정책으로 주차비 부담을 덜 수 있어요", "평생 주유 무료로 하기", "기름값이 너무 많이 올랐어 주유비가 많이 들어 고민이야", "전기차는 기름값 걱정 없어요", "주유비 부담 없이 마음껏 드라이브를 즐길 수 있어요"},
                {"무채색 차가 좋다", "검은색, 흰색, 회색! 오래 타려면 무난한 게 최고야", "기본 색감도 다채롭게", "무채색 컬러도 매트부터 메탈릭까지 다양한 질감으로 구성했어요", "컬러풀한 차가 좋다", "무채색은 지루해! 내가 좋아하는 색으로 고를래", "신규 색상 5종 출시", "기존 캐스퍼 색상 라인업에 새로운 5종을 추가! 내 차의 개성을 뽐내봐요"},
                {"주말에는 바다로 가기", "아까운 주말엔 국내여행이라도 다녀오고 싶어", "충전 한 번에 315km", "엔트리급 전기차의 주행거리 혁신 한 번 충전으로 서울에서 강릉까지 왕복도 거뜬해요", "주말에는 도심 드라이브", "평일에 너무 피곤했으니 오랜만에 동네 드라이브나 할래", "충전 한 번에 315km", "엔트리급 전기차의 주행거리 혁신 한 번 충전으로 서울에서 강릉까지 왕복도 거뜬해요"}
        };

        List<RushEvent> rushEvents = new ArrayList<>();

        for (int i = 0; i < texts.length; i++) {
            // 각 이벤트의 날짜를 오늘 기준으로 설정
            RushEvent rushEvent = new RushEvent(
                    startDateTime.plusDays(i),  // 이벤트 시작 날짜
                    endDateTime.plusDays(i),    // 이벤트 종료 날짜
                    3,                          // 우승자 수 (winnerCount)
                    "http://example.com/prize" + (i + 1) + ".jpg",  // 상 이미지 URL
                    "Prize Description " + (i + 1)                  // 상 설명
            );

            // RushOption 생성
            RushOption option1 = new RushOption(
                    rushEvent,
                    texts[i][0],  // Option 1 Main Text
                    texts[i][1],  // Option 1 Sub Text
                    texts[i][2],  // Option 1 Result Main Text
                    texts[i][3],  // Option 1 Result Sub Text
                    "http://example.com/option1-image" + (i + 1) + ".jpg",
                    Position.LEFT
            );

            RushOption option2 = new RushOption(
                    rushEvent,
                    texts[i][4],  // Option 2 Main Text
                    texts[i][5],  // Option 2 Sub Text
                    texts[i][6],  // Option 2 Result Main Text
                    texts[i][7],  // Option 2 Result Sub Text
                    "http://example.com/option2-image" + (i + 1) + ".jpg",
                    Position.RIGHT
            );

            // RushEvent의 options 컬렉션에 추가
            rushEvent.getOptions().add(option1);
            rushEvent.getOptions().add(option2);

            // RushEvent 및 RushOption 저장
            rushEvent = rushEventRepository.save(rushEvent);
            rushOptionRepository.save(option1);
            rushOptionRepository.save(option2);

            rushEvents.add(rushEvent);
        }

        eventCacheService.setCacheValue(LocalDate.now());
        eventCacheService.setAllRushEvent();
        rushEventRedisService.clearAllrushEventRate();
    }


    // 오늘의 이벤트 옵션 정보를 반환
    public RushEventResponseDto getTodayRushEventOptions() {
        LocalDate today = LocalDate.now();
        RushEventResponseDto todayEvent = eventCacheService.getTodayEvent(today);
        Set<RushEventOptionResponseDto> options = todayEvent.getOptions();

        RushEventOptionResponseDto leftOption = options.stream()
                .filter(option -> option.getPosition() == Position.LEFT)
                .findFirst()
                .orElseThrow(() -> new CustomException("왼쪽 선택지가 존재하지 않습니다.", CustomErrorCode.INVALID_RUSH_EVENT_OPTIONS_COUNT));

        RushEventOptionResponseDto rightOption = options.stream()
                .filter(option -> option.getPosition() == Position.RIGHT)
                .findFirst()
                .orElseThrow(() -> new CustomException("오른쪽 선택지가 존재하지 않습니다.", CustomErrorCode.INVALID_RUSH_EVENT_OPTIONS_COUNT));

        return RushEventResponseDto.withMainOption(leftOption, rightOption);
    }

    public RushEventOptionResponseDto getRushEventOptionResult(int optionId) {
        Position position = Position.of(optionId);
        LocalDate today = LocalDate.now();
        RushEventResponseDto todayEvent = eventCacheService.getTodayEvent(today);
        Set<RushEventOptionResponseDto> options = todayEvent.getOptions();

        if (options.size() != 2) {
            throw new CustomException("해당 이벤트의 선택지가 2개가 아닙니다.", CustomErrorCode.INVALID_RUSH_EVENT_OPTIONS_COUNT);
        }

        RushEventOptionResponseDto selectedOption = options.stream()
                .filter(option -> option.getPosition() == position)
                .findFirst()
                .orElseThrow(() -> new CustomException("사용자가 선택한 선택지가 존재하지 않습니다.", CustomErrorCode.NO_RUSH_EVENT_OPTION));

        return RushEventOptionResponseDto.inResult(selectedOption);
    }
}
