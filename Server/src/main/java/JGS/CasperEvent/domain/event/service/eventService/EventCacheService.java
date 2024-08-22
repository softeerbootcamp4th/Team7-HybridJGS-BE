package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.RushEventResponseDto;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventCacheService {

    private final RushEventRepository rushEventRepository;
    private final LotteryEventRepository lotteryEventRepository;

    @Cacheable(value = "ongoingLotteryEvent")
    public LotteryEvent getLotteryEvent(){
        return fetchOngoingLotteryEvent();
    }

    @CachePut(value = "ongoingLotteryEvent")
    public LotteryEvent setLotteryEvent() {
        // 오늘 날짜에 해당하는 모든 이벤트 꺼내옴
        return fetchOngoingLotteryEvent();
    }

    private LotteryEvent fetchOngoingLotteryEvent() {
        // 오늘 날짜에 해당하는 모든 이벤트 꺼내옴
        List<LotteryEvent> lotteryEventList = lotteryEventRepository.findAll();

        if (lotteryEventList.isEmpty()) {
            throw new CustomException(CustomErrorCode.NO_LOTTERY_EVENT);
        }

        if (lotteryEventList.size() > 1) {
            throw new CustomException(CustomErrorCode.TOO_MANY_LOTTERY_EVENT);
        }

        return lotteryEventList.get(0);
    }

    @Cacheable(value = "todayEventCache", key = "#today")
    public RushEventResponseDto getTodayEvent(LocalDate today) {
        log.info("오늘의 이벤트 캐싱 {}", today);
        // 오늘 날짜에 해당하는 모든 이벤트 꺼내옴
        return fetchTodayRushEvent(today);
    }

    @CachePut(value = "todayEventCache", key = "#today")
    public RushEventResponseDto setCacheValue(LocalDate today) {
        log.info("이벤트 업데이트 {}", today);
        return fetchTodayRushEvent(today);
    }

    private RushEventResponseDto fetchTodayRushEvent(LocalDate today) {
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
}
