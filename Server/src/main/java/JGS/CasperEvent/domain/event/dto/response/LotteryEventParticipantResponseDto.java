package JGS.CasperEvent.domain.event.dto.response;

import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;

import java.time.LocalDateTime;

public class LotteryEventParticipantResponseDto {
    private Long id;
    private String phoneNumber;
    private int linkClickedCount;
    private int expectations;
    private int appliedCount;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;


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

}
