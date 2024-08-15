package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import JGS.CasperEvent.domain.event.entity.participants.LotteryWinners;

import java.time.LocalDate;
import java.time.LocalTime;

public record LotteryEventWinnerResponseDto(
        Long id, String phoneNumber, int linkClickedCounts,
        int expectation, int appliedCount, long ranking,
        LocalDate createdDate, LocalTime createdTime) {

    public static LotteryEventWinnerResponseDto of(LotteryWinners lotteryWinner) {
        return new LotteryEventWinnerResponseDto(
                lotteryWinner.getId(),
                lotteryWinner.getPhoneNumber(),
                lotteryWinner.getLinkClickedCount(),
                lotteryWinner.getExpectation(),
                lotteryWinner.getAppliedCount(),
                lotteryWinner.getRanking(),
                lotteryWinner.getCreatedAt().toLocalDate(),
                lotteryWinner.getCreatedAt().toLocalTime()
        );
    }
}