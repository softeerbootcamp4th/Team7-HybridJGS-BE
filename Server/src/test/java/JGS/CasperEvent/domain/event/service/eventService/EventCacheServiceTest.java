package JGS.CasperEvent.domain.event.service.eventService;


import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventCacheServiceTest {

    @Mock
    private RushEventRepository rushEventRepository;

    @Mock
    private LotteryEventRepository lotteryEventRepository;

    @InjectMocks
    EventCacheService eventCacheService;

    @Test
    @DisplayName("추첨 이벤트 조회 테스트 - 성공")
    void getLotteryEventTest_Success() {
        //given
        LotteryEvent lotteryEvent = new LotteryEvent();
        List<LotteryEvent> lotteryEventList = List.of(lotteryEvent);
        given(lotteryEventRepository.findAll()).willReturn(lotteryEventList);

        //when
        LotteryEvent actualLotteryEvent = eventCacheService.getLotteryEvent();

        //then
        assertThat(actualLotteryEvent).isEqualTo(lotteryEvent);
    }

}