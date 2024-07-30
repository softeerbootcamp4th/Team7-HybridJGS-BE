package JGS.CasperEvent.domain.event.dto;

import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;

import java.time.LocalDate;

public record GetLotteryEvent(Long lotteryEventId, LocalDate startDate, LocalDate endDate,
                              int winnerCount) {
    public static GetLotteryEvent of(LotteryEvent lotteryEvent) {
        return new GetLotteryEvent(
                lotteryEvent.getLotteryEventId(),
                lotteryEvent.getStartDate(),
                lotteryEvent.getEndDate(),
                lotteryEvent.getWinnerCount()
        );
    }
}
