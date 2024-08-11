package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.ResponseDto.*;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.entity.event.RushOption;
import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushOptionRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.RushParticipantsRepository;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.CustomErrorCode;
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

@Service
@RequiredArgsConstructor
public class RushEventService {
    private final RushEventRepository rushEventRepository;
    private final RushParticipantsRepository rushParticipantsRepository;
    private final RedisTemplate<String, RushEvent> rushEventRedisTemplate;
    private final RushOptionRepository rushOptionRepository;

    @Transactional
    public RushEventListResponseDto getAllRushEvents() {
        // 오늘의 선착순 이벤트 꺼내오기
        RushEvent todayEvent = rushEventRedisTemplate.opsForValue().get("todayEvent");

        // 오늘의 선착순 이벤트가 redis에 등록되지 않은 경우
        if (todayEvent == null) {
            throw new CustomException("오늘의 선착순 이벤트가 redis에 등록되지 않았습니다.", CustomErrorCode.TODAY_RUSH_EVENT_NOT_FOUND);
        }

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
                todayEvent.getRushEventId(),
                totalStartDate,
                totalEndDate,
                activePeriod
                );
    }

    // 응모 여부 조회
    public boolean isExists(Long eventId, String userId) {
        return rushParticipantsRepository.existsByRushEvent_RushEventIdAndBaseUser_Id(eventId, userId);
    }

    @Transactional
    public void apply(BaseUser user, Long eventId, int optionId) {
        // 이미 응모한 회원인지 검증
        if (rushParticipantsRepository.existsByRushEvent_RushEventIdAndBaseUser_Id(eventId, user.getId())) {
            throw new CustomException("이미 응모한 회원입니다.", CustomErrorCode.CONFLICT);
        }

        // eventId 를 이용하여 rushEvent 를 꺼냄
        RushEvent rushEvent = RepositoryErrorHandler.findByIdOrElseThrow(rushEventRepository, eventId, CustomErrorCode.NO_RUSH_EVENT);

        // 새로운 RushParticipants 를 생성하여 DB 에 저장
        RushParticipants rushParticipants = new RushParticipants(user, rushEvent, optionId);
        rushParticipantsRepository.save(rushParticipants);
    }

    public RushEventRateResponseDto getRushEventRate(Long eventId) {
        long leftOptionCount = rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(eventId, 1);
        long rightOptionCount = rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(eventId, 2);

        return new RushEventRateResponseDto(leftOptionCount, rightOptionCount);
    }

    // 이벤트 결과를 반환
    // 해당 요청은 무조건 응모한 유저일 때만 요청 가능하다고 가정
    @Transactional
    public RushEventResultResponseDto getRushEventResult(BaseUser user, Long eventId) {
        // 최종 선택 비율을 조회
        // TODO: 레디스에 캐시
        RushEventRateResponseDto rushEventRateResponseDto = getRushEventRate(eventId);

        // 해당 이벤트의 당첨자 수를 가져옴
        int winnerCount = rushEventRepository.findByRushEventId(eventId).getWinnerCount();

        // 해당 유저가 응모한 optionId 가져옴
        Optional<Integer> optionId = rushParticipantsRepository.getOptionIdByUserId(user.getId());

        if (optionId.isEmpty()) {
            throw new CustomException("해당 유저가 이벤트 응모를 하지 않았습니다.", CustomErrorCode.USER_NOT_FOUND);
        }

        // eventId, userId, optionId 를 이용하여 해당 유저가 응모한 선택지에서 등수를 가져옴
        long rank = rushParticipantsRepository.findUserRankByEventIdAndUserIdAndOptionId(eventId, user.getId(), optionId.get());

        // 해당 선택지를 선택한 모든 유저 수를 가져옴
        long totalParticipants = rushParticipantsRepository.countAllByOptionId(optionId.get());

        return new RushEventResultResponseDto(rushEventRateResponseDto.getLeftOption(), rushEventRateResponseDto.rightOption(), rank, totalParticipants, winnerCount);
    }

    @Transactional
    // 오늘의 이벤트를 DB에 꺼내서 반환
    public RushEvent getTodayRushEvent(LocalDate today) {
        // 오늘 날짜에 해당하는 모든 이벤트 꺼내옴
        List<RushEvent> rushEventList = rushEventRepository.findByEventDate(today);

        if (rushEventList.isEmpty()) {
            throw new CustomException("선착순 이벤트가 존재하지않습니다.", CustomErrorCode.NO_RUSH_EVENT);
        }

        if (rushEventList.size() > 1) {
            throw new CustomException("선착순 이벤트가 존재하지않습니다.", CustomErrorCode.MULTIPLE_RUSH_EVENTS_FOUND);
        }

        return rushEventList.get(0);
    }

    @Transactional
    public void setTodayEventToRedis() {
        // 테이블 초기화
        rushParticipantsRepository.deleteAllInBatch();
        rushOptionRepository.deleteAllInBatch();
        rushEventRepository.deleteAllInBatch();

        LocalDateTime startDateTime = LocalDateTime.of(2024, 8, 11, 22, 0);
        LocalDateTime endDateTime = startDateTime.plusMinutes(10);

        List<RushEvent> rushEvents = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            // RushEvent 생성 및 초기화
            RushEvent rushEvent = new RushEvent(
                    startDateTime.plusDays(i),  // 이벤트 시작 날짜
                    endDateTime.plusDays(i),    // 이벤트 종료 날짜
                    0,                          // 우승자 수 (winnerCount)
                    "http://example.com/prize" + (i + 1) + ".jpg",  // 상 이미지 URL
                    "Prize Description " + (i + 1)                  // 상 설명
            );

            // RushEvent 저장
            rushEvent = rushEventRepository.save(rushEvent);
            rushEvents.add(rushEvent);

            // 첫 번째 RushOption 생성 및 저장
            RushOption option1 = new RushOption(
                    rushEvent,
                    "Option 1 Main Text for Event " + (i + 1),
                    "Option 1 Sub Text for Event " + (i + 1),
                    "Option 1 Result Main Text for Event " + (i + 1),
                    "Option 1 Result Sub Text for Event " + (i + 1),
                    "http://example.com/option1-image" + (i + 1) + ".jpg"
            );
            rushOptionRepository.save(option1);

            // 두 번째 RushOption 생성 및 저장
            RushOption option2 = new RushOption(
                    rushEvent,
                    "Option 2 Main Text for Event " + (i + 1),
                    "Option 2 Sub Text for Event " + (i + 1),
                    "Option 2 Result Main Text for Event " + (i + 1),
                    "Option 2 Result Sub Text for Event " + (i + 1),
                    "http://example.com/option2-image" + (i + 1) + ".jpg"
            );
            rushOptionRepository.save(option2);
        }

        // 처음으로 생성된 RushEvent를 Redis에 저장
        rushEventRedisTemplate.opsForValue().set("todayEvent", rushEvents.get(0));
    }
}
