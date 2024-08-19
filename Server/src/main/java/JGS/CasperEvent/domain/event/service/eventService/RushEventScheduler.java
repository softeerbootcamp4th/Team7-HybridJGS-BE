package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.RushEventResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class RushEventScheduler {

    private final RushEventService rushEventService;
    private final RedisTemplate<String, RushEventResponseDto> rushEventRedisTemplate;

    // 매일 0시 1분에 스케줄된 작업을 실행합니다.
    @Scheduled(cron = "0 1 0 * * ?")
    public void fetchDailyEvents() {
        // EventService를 통해 오늘의 이벤트를 가져옵니다.
        RushEventResponseDto todayEvent = rushEventService.getTodayRushEventFromRDB();

        // 가져온 이벤트에 대한 추가 작업을 수행합니다.
        // 예: 캐싱, 로그 기록, 알림 발송 등
        rushEventRedisTemplate.opsForValue().set("todayEvent", todayEvent);

        // 로그 출력
        log.info("선착순 이벤트 스케줄러 실행: {}", LocalDateTime.now());
        log.info("가져온 이벤트 날짜: {}", todayEvent.startDateTime());
    }
}
