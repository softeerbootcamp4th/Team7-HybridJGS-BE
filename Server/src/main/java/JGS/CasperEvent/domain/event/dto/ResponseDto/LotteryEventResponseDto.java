package JGS.CasperEvent.domain.event.dto.ResponseDto;

import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record LotteryEventResponseDto(LocalDateTime serverDateTime, LocalDateTime eventStartDate,
                                      LocalDateTime eventEndDate,
                                      long activePeriod) {
    public static LotteryEventResponseDto of(LotteryEvent lotteryEvent, LocalDateTime serverDateTime) {
        return new LotteryEventResponseDto(
                serverDateTime,
                lotteryEvent.getStartDateTime(),
                lotteryEvent.getEndDateTime(),
                ChronoUnit.DAYS.between(lotteryEvent.getStartDateTime(), lotteryEvent.getEndDateTime())
        );
    }
}
