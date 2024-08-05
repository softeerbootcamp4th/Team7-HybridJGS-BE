package JGS.CasperEvent.domain.event.dto.ResponseDto;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;

import java.time.LocalDateTime;


public record GetLotteryParticipant(int linkClickedCount, int expectations, int appliedCount,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static GetLotteryParticipant of(LotteryParticipants lotteryParticipants) {
        return new GetLotteryParticipant(
                lotteryParticipants.getLinkClickedCount(),
                lotteryParticipants.getExpectations(),
                lotteryParticipants.getAppliedCount(),
                lotteryParticipants.getCreatedAt(),
                lotteryParticipants.getUpdatedAt()
        );
    }
}