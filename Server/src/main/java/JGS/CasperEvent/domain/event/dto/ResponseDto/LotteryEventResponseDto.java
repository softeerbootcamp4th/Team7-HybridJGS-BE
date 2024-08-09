package JGS.CasperEvent.domain.event.dto.ResponseDto;

import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record LotteryEventResponseDto(LocalDateTime serverDateTime, LocalDate eventStartDate, LocalDate eventEndDate,
                                      long activePeriod) {
    public static LotteryEventResponseDto of(LocalDateTime serverDateTime, LotteryEvent lotteryEvent) {
        return new LotteryEventResponseDto(
                serverDateTime,
                lotteryEvent.getEventStartDate(),
                lotteryEvent.getEventEndDate(),
                ChronoUnit.DAYS.between(lotteryEvent.getEventStartDate(), lotteryEvent.getEventEndDate())
        );
    }

}
