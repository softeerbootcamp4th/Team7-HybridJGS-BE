package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.RushEventResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class RushEventScheduler {

    private final RushEventService rushEventService;
    private final RedisTemplate<String, RushEventResponseDto> rushEventRedisTemplate;

    // 매일 12시에 스케줄된 작업을 실행합니다.
    @Scheduled(cron = "0 0 12 * * ?")
    public void fetchDailyEvents() {
        // 오늘의 날짜를 구합니다.
        LocalDate today = LocalDate.now();

        // EventService를 통해 오늘의 이벤트를 가져옵니다.
        RushEventResponseDto todayEvent = rushEventService.getTodayRushEvent(today);

        // 가져온 이벤트에 대한 추가 작업을 수행합니다.
        // 예: 캐싱, 로그 기록, 알림 발송 등
        rushEventRedisTemplate.opsForValue().set("todayEvent", todayEvent);
    }
}
