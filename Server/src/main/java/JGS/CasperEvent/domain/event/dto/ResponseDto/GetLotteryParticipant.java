package JGS.CasperEvent.domain.event.dto.ResponseDto;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;

import java.time.LocalDateTime;


public record GetLotteryParticipant(int linkClickedCount, int expectations, int appliedCount, CasperBotResponseDto casperBot,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static GetLotteryParticipant of(LotteryParticipants lotteryParticipants, CasperBotResponseDto getcasperBotResponseDto) {
        return new GetLotteryParticipant(
                lotteryParticipants.getLinkClickedCount(),
                lotteryParticipants.getExpectations(),
                lotteryParticipants.getAppliedCount(),
                getcasperBotResponseDto,
                lotteryParticipants.getBaseUser().getCreatedAt(),
                lotteryParticipants.getBaseUser().getUpdatedAt()
        );
    }
}