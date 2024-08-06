package JGS.CasperEvent.domain.event.dto.ResponseDto;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;

import java.time.LocalDateTime;


public record GetLotteryParticipant(int linkClickedCount, int expectations, int appliedCount, GetCasperBot casperBot,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static GetLotteryParticipant of(LotteryParticipants lotteryParticipants, GetCasperBot getcasperBot) {
        return new GetLotteryParticipant(
                lotteryParticipants.getLinkClickedCount(),
                lotteryParticipants.getExpectations(),
                lotteryParticipants.getAppliedCount(),
                getcasperBot,
                lotteryParticipants.getBaseUser().getCreatedAt(),
                lotteryParticipants.getBaseUser().getUpdatedAt()
        );
    }
}