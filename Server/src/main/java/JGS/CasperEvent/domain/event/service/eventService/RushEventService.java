package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.*;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.entity.event.RushOption;
import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushOptionRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.RushParticipantsRepository;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.enums.Position;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.util.RepositoryErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, RushEventResponseDto> rushEventRedisTemplate;
    private final RushOptionRepository rushOptionRepository;

    @Transactional
    public RushEventListResponseDto getAllRushEvents() {
        // 오늘의 선착순 이벤트 꺼내오기
        RushEventResponseDto todayEvent = getTodayRushEventFromRedis();

        // DB에서 모든 RushEvent 가져오기
        List<RushEvent> rushEventList = rushEventRepository.findAll();

        // 선착순 이벤트 전체 시작 날짜와 종료 날짜 구하기
        List<LocalDate> dates = rushEventList.stream().map(rushEvent -> rushEvent.getStartDateTime().toLocalDate()).sorted().toList();

        LocalDate totalStartDate = dates.get(0);
        LocalDate totalEndDate = dates.get(dates.size() - 1);

        // 전체 이벤트 기간 구하기
        long activePeriod = totalStartDate.until(totalEndDate).getDays() + 1;

        // RushEvent를 DTO로 전환
        List<MainRushEventResponseDto> mainRushEventDtoList = rushEventList.stream()
                .map(MainRushEventResponseDto::of)
                .toList();

        // DTO 리스트와 서버 시간을 담은 RushEventListAndServerTimeResponse 객체 생성 후 반환
        return new RushEventListResponseDto(
                mainRushEventDtoList,
                LocalDateTime.now(),
                todayEvent.rushEventId(),
                totalStartDate,
                totalEndDate,
                activePeriod
        );
    }

    // 응모 여부 조회
    public boolean isExists(String userId) {
        Long todayEventId = getTodayRushEventFromRedis().rushEventId();
        return rushParticipantsRepository.existsByRushEvent_RushEventIdAndBaseUser_Id(todayEventId, userId);
    }

    @Transactional
    public void apply(BaseUser user, int optionId) {
        Long todayEventId = getTodayRushEventFromRedis().rushEventId();

        // 이미 응모한 회원인지 검증
        if (rushParticipantsRepository.existsByRushEvent_RushEventIdAndBaseUser_Id(todayEventId, user.getId())) {
            throw new CustomException("이미 응모한 회원입니다.", CustomErrorCode.CONFLICT);
        }

        // eventId 를 이용하여 rushEvent 를 꺼냄
        RushEvent rushEvent = RepositoryErrorHandler.findByIdOrElseThrow(rushEventRepository, todayEventId, CustomErrorCode.NO_RUSH_EVENT);

        // 새로운 RushParticipants 를 생성하여 DB 에 저장
        RushParticipants rushParticipants = new RushParticipants(user, rushEvent, optionId);
        rushParticipantsRepository.save(rushParticipants);
    }

    // 진행중인 게임의 응모 비율 반환
    public RushEventRateResponseDto getRushEventRate(BaseUser user) {
        Long todayEventId = getTodayRushEventFromRedis().rushEventId();
        Optional<Integer> optionId = rushParticipantsRepository.getOptionIdByUserId(user.getId());
        long leftOptionCount = rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(todayEventId, 1);
        long rightOptionCount = rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(todayEventId, 2);

        return new RushEventRateResponseDto(
                optionId.orElseThrow(() -> new CustomException("유저가 응모한 선택지가 존재하지 않습니다.", CustomErrorCode.USER_NOT_FOUND)),
                leftOptionCount, rightOptionCount);
    }

    // 이벤트 결과를 반환
    // 해당 요청은 무조건 응모한 유저일 때만 요청 가능하다고 가정
    @Transactional
    public RushEventResultResponseDto getRushEventResult(BaseUser user) {
        RushEventResponseDto todayRushEvent = getTodayRushEventFromRedis();

        // 최종 선택 비율을 조회
        // TODO: 레디스에 캐시
        RushEventRateResponseDto rushEventRateResponseDto = getRushEventRate(user);
        long leftOption = rushEventRateResponseDto.leftOption();
        long rightOption = rushEventRateResponseDto.rightOption();

        // 동점인 경우
        if (leftOption == rightOption) {
            // 전체 참여자에서 등수 계산하기
            long rank = rushParticipantsRepository.findUserRankByEventIdAndUserId(todayRushEvent.rushEventId(), user.getId());

            // 각 옵션 선택지를 더하여 전체 참여자 수 구하기
            long totalParticipants = rushEventRateResponseDto.leftOption() + rushEventRateResponseDto.rightOption();

            // 당첨 여부
            boolean isWinner = rank <= todayRushEvent.winnerCount();

            return new RushEventResultResponseDto(rushEventRateResponseDto, rank, totalParticipants, isWinner);
        }

        // 해당 유저가 선택한 옵션을 가져옴
        int optionId = rushEventRateResponseDto.optionId();

        long totalParticipants = (optionId == 1 ? leftOption : rightOption);

        // eventId, userId, optionId 를 이용하여 해당 유저가 응모한 선택지에서 등수를 가져옴
        long rank = rushParticipantsRepository.findUserRankByEventIdAndUserIdAndOptionId(todayRushEvent.rushEventId(), user.getId(), optionId);

        // 해당 유저가 선택한 옵션이 패배한 경우
        if ((optionId == 1 && leftOption < rightOption) || (optionId == 2 && leftOption > rightOption)) {
            return new RushEventResultResponseDto(rushEventRateResponseDto, rank, totalParticipants, false);
        }

        // 당첨 여부
        boolean isWinner = rank <= todayRushEvent.winnerCount();

        return new RushEventResultResponseDto(rushEventRateResponseDto, rank, totalParticipants, isWinner);
    }

    // 오늘의 이벤트를 DB에 꺼내서 반환
    public RushEventResponseDto getTodayRushEventFromRDB() {
        LocalDate today = LocalDate.now();

        // 오늘 날짜에 해당하는 모든 이벤트 꺼내옴
        List<RushEvent> rushEventList = rushEventRepository.findByEventDate(today);

        if (rushEventList.isEmpty()) {
            throw new CustomException("선착순 이벤트가 존재하지않습니다.", CustomErrorCode.NO_RUSH_EVENT);
        }

        if (rushEventList.size() > 1) {
            throw new CustomException("선착순 이벤트가 2개 이상 존재합니다.", CustomErrorCode.MULTIPLE_RUSH_EVENTS_FOUND);
        }

        return RushEventResponseDto.of(rushEventList.get(0));
    }

    // 오늘의 이벤트 꺼내오기
    private RushEventResponseDto getTodayRushEventFromRedis() {
        RushEventResponseDto todayEvent = rushEventRedisTemplate.opsForValue().get("todayEvent");

        // Redis에 오늘의 이벤트가 없으면 DB에서 가져와서 Redis에 저장한 후 반환.
        if (todayEvent == null) {
            todayEvent = getTodayRushEventFromRDB();
            rushEventRedisTemplate.opsForValue().set("todayEvent", todayEvent);
        }

        return todayEvent;
    }

    @Transactional
    public void setTodayEventToRedis() {
        // 테이블 초기화
        rushParticipantsRepository.deleteAllInBatch();
        rushOptionRepository.deleteAllInBatch();
        rushEventRepository.deleteAllInBatch();

        // 오늘의 날짜를 기준으로 시간 설정
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(2).withHour(22).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDateTime = startDateTime.plusMinutes(10);

        List<RushEvent> rushEvents = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
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
                    "Option 1 Main Text for Event " + (i + 1),
                    "Option 1 Sub Text for Event " + (i + 1),
                    "Option 1 Result Main Text for Event " + (i + 1),
                    "Option 1 Result Sub Text for Event " + (i + 1),
                    "http://example.com/option1-image" + (i + 1) + ".jpg",
                    Position.LEFT
            );

            RushOption option2 = new RushOption(
                    rushEvent,
                    "Option 2 Main Text for Event " + (i + 1),
                    "Option 2 Sub Text for Event " + (i + 1),
                    "Option 2 Result Main Text for Event " + (i + 1),
                    "Option 2 Result Sub Text for Event " + (i + 1),
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

        // 세 번째로 생성된 RushEvent를 Redis에 저장
        rushEventRedisTemplate.opsForValue().set("todayEvent", RushEventResponseDto.of(rushEvents.get(2)));
    }

    // 오늘의 이벤트 옵션 정보를 반환
    public MainRushEventOptionsResponseDto getTodayRushEventOptions() {
        RushEventResponseDto todayEvent = getTodayRushEventFromRedis();
        Set<RushEventOptionResponseDto> options = todayEvent.options();

        RushEventOptionResponseDto leftOption = options.stream()
                .filter(option -> option.position() == Position.LEFT)
                .findFirst()
                .orElseThrow(() -> new CustomException("왼쪽 선택지가 존재하지 않습니다.", CustomErrorCode.INVALID_RUSH_EVENT_OPTIONS_COUNT));

        RushEventOptionResponseDto rightOption = options.stream()
                .filter(option -> option.position() == Position.RIGHT)
                .findFirst()
                .orElseThrow(() -> new CustomException("오른쪽 선택지가 존재하지 않습니다.", CustomErrorCode.INVALID_RUSH_EVENT_OPTIONS_COUNT));

        return new MainRushEventOptionsResponseDto(
                MainRushEventOptionResponseDto.of(leftOption),
                MainRushEventOptionResponseDto.of(rightOption)
        );
    }

    public ResultRushEventOptionResponseDto getRushEventOptionResult(int optionId) {
        Position position = Position.of(optionId);
        RushEventResponseDto todayEvent = getTodayRushEventFromRedis();
        Set<RushEventOptionResponseDto> options = todayEvent.options();

        if (options.size() != 2) {
            throw new CustomException("해당 이벤트의 선택지가 2개가 아닙니다.", CustomErrorCode.INVALID_RUSH_EVENT_OPTIONS_COUNT);
        }

        RushEventOptionResponseDto selectedOption = options.stream()
                .filter(option -> option.position() == position)
                .findFirst()
                .orElseThrow(() -> new CustomException("사용자가 선택한 선택지가 존재하지 않습니다.", CustomErrorCode.NO_RUSH_EVENT_OPTION));

        return new ResultRushEventOptionResponseDto(
                selectedOption.mainText(),
                selectedOption.resultMainText(),
                selectedOption.resultSubText()
        );
    }
}
