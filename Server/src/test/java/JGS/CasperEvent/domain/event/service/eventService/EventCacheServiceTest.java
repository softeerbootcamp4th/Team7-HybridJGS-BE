package JGS.CasperEvent.domain.event.service.eventService;


import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.error.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    @DisplayName("추첨 이벤트 업데이트 테스트 - 성공")
    void setLotteryEventTest_Success() {
        //given
        LotteryEvent lotteryEvent = new LotteryEvent();
        List<LotteryEvent> lotteryEventList = List.of(lotteryEvent);
        given(lotteryEventRepository.findAll()).willReturn(lotteryEventList);

        //when
        LotteryEvent actualLotteryEvent = eventCacheService.getLotteryEvent();

        //then
        assertThat(actualLotteryEvent).isEqualTo(lotteryEvent);
    }

    @Test
    @DisplayName("추첨 이벤트 조회 테스트 - 실패 (이벤트 없음)")
    void getLotteryEventTest_Failure_NoLotteryEvent() {
        //given
        given(lotteryEventRepository.findAll()).willReturn(new ArrayList<>());

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                eventCacheService.getLotteryEvent()
        );

        //then
        assertEquals(CustomErrorCode.NO_LOTTERY_EVENT, exception.getErrorCode());
        assertEquals("추첨 이벤트를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("추첨 이벤트 조회 테스트 - 실패 (이벤트 없음)")
    void getLotteryEventTest_Failure_TooManyLotteryEvent() {
        //given
        List lotteryEventList = List.of(
                new LotteryEvent(), new LotteryEvent()
        );
        given(lotteryEventRepository.findAll()).willReturn(lotteryEventList);

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                eventCacheService.getLotteryEvent()
        );

        //then
        assertEquals(CustomErrorCode.TOO_MANY_LOTTERY_EVENT, exception.getErrorCode());
        assertEquals("현재 진행중인 추첨 이벤트가 2개 이상입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("선착순 이벤트 조회 테스트 - 성공")
    void getTodayEventTest_Success() {
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