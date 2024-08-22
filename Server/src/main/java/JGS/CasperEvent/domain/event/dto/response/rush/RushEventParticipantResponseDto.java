package JGS.CasperEvent.domain.event.dto.response.rush;

import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.LotteryEventWinnerResponseDto;
import JGS.CasperEvent.domain.event.entity.participants.LotteryWinners;
import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;

import java.time.LocalDate;
import java.time.LocalTime;

public class RushEventParticipantResponseDto {
    private Long id;
    private String phoneNumber;
    private int linkClickedCounts;
    private int expectation;
    private int appliedCount;
    private Long ranking;
    private LocalDate createdDate;
    private LocalTime createdTime;
    private int balanceGameChoice;
    private Long rank;

    private RushEventParticipantResponseDto(Long id, String phoneNumber,
                                            int linkClickedCounts, int expectation,
                                            int appliedCount, Long ranking,
                                            LocalDate createdDate, LocalTime createdTime) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.linkClickedCounts = linkClickedCounts;
        this.expectation = expectation;
        this.appliedCount = appliedCount;
        this.ranking = ranking;
        this.createdDate = createdDate;
        this.createdTime = createdTime;
    }

    // LotteryEventWinnerResponseDto
    public static RushEventParticipantResponseDto winner(LotteryWinners lotteryWinner) {
        return new RushEventParticipantResponseDto(
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

    private RushEventParticipantResponseDto(Long id, String phoneNumber,
                                            int balanceGameChoice, LocalDate createdDate,
                                            LocalTime createdTime, Long rank) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.balanceGameChoice = balanceGameChoice;
        this.createdDate = createdDate;
        this.createdTime = createdTime;
        this.rank = rank;
    }

    // RushEventParticipantResponseDto
    public static RushEventParticipantResponseDto result(RushParticipants rushParticipants, Long rank) {
        return new RushEventParticipantResponseDto(
                rushParticipants.getId(),
                rushParticipants.getBaseUser().getId(),
                rushParticipants.getOptionId(),
                rushParticipants.getCreatedAt().toLocalDate(),
                rushParticipants.getCreatedAt().toLocalTime(),
                rank
        );
    }
}
