package JGS.CasperEvent.domain.event.dto.ResponseDto;

import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;

import java.time.LocalDate;

public record LotteryEventResponseDto(Long lotteryEventId, LocalDate startDate, LocalDate endDate,
                                      int winnerCount) {
    public static LotteryEventResponseDto of(LotteryEvent lotteryEvent) {
        return new LotteryEventResponseDto(
                lotteryEvent.getLotteryEventId(),
                lotteryEvent.getStartDate(),
                lotteryEvent.getEndDate(),
                lotteryEvent.getWinnerCount()
        );
    }
}
