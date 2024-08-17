package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.RushEventResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RushEventSchedulerTest {

    @Mock
    private RushEventService rushEventService;

    @Mock
    private RedisTemplate<String, RushEventResponseDto> rushEventRedisTemplate;

    @Mock
    private ValueOperations<String, RushEventResponseDto> valueOperations;

    @InjectMocks
    private RushEventScheduler rushEventScheduler;

    @Test
    void fetchDailyEvents_ShouldCacheTodayEventInRedis() {
        // given
        LocalDate today = LocalDate.now();
        RushEventResponseDto todayEvent = new RushEventResponseDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                315,
                "image-url",
                "prize-description",
                new HashSet<>()
        );

        given(rushEventRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(rushEventService.getTodayRushEvent(today)).willReturn(todayEvent);

        // when
        rushEventScheduler.fetchDailyEvents();

        // then
        verify(rushEventService).getTodayRushEvent(today);
        verify(rushEventRedisTemplate.opsForValue()).set("todayEvent", todayEvent);
    }
}
