package JGS.CasperEvent.domain.event.dto.response;

import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class LotteryEventParticipantResponseDto {
    private Long id;
    private String phoneNumber;

    // 이름이 비슷하니 통일 필요
    private int linkClickedCount;
    private int expectations;

    // 디테일에서 쓰는 변수
    private int linkClickedCounts;
    private int expectation;

    private int appliedCount;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    LocalDate createdDate;
    LocalTime createdTime;


    private LotteryEventParticipantResponseDto(
            int linkClickedCount, int expectations, int appliedCount,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.linkClickedCount = linkClickedCount;
        this.expectations = expectations;
        this.appliedCount = appliedCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LotteryEventParticipantResponseDto of(LotteryParticipants lotteryParticipants) {
        return new LotteryEventParticipantResponseDto(
                lotteryParticipants.getLinkClickedCount(),
                lotteryParticipants.getExpectations(),
                lotteryParticipants.getAppliedCount(),
                lotteryParticipants.getCreatedAt(),
                lotteryParticipants.getUpdatedAt()
        );
    }

    private LotteryEventParticipantResponseDto(
            Long id, String phoneNumber, int linkClickedCounts,
            int expectation, int appliedCount,
            LocalDate createdDate, LocalTime createdTime) {
        this.id = id;
        this.phoneNumber =phoneNumber;
        this.linkClickedCounts = linkClickedCounts;
        this.expectation = expectation;
        this.appliedCount = appliedCount;
        this.createdDate = createdDate;
        this.createdTime = createdTime;
    }

    public static LotteryEventParticipantResponseDto withDetail(LotteryParticipants lotteryParticipants) {
        return new LotteryEventParticipantResponseDto(
                lotteryParticipants.getId(),
                lotteryParticipants.getBaseUser().getId(),
                lotteryParticipants.getLinkClickedCount(),
                lotteryParticipants.getExpectations(),
                lotteryParticipants.getAppliedCount(),
                lotteryParticipants.getCreatedAt().toLocalDate(),
                lotteryParticipants.getCreatedAt().toLocalTime()
        );
    }

}
