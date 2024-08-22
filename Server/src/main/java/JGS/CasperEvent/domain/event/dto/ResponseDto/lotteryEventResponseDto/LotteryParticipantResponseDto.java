package JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto;
import JGS.CasperEvent.domain.event.dto.response.CasperBotResponseDto;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;

import java.time.LocalDateTime;


public record LotteryParticipantResponseDto(int linkClickedCount, int expectations, int appliedCount, CasperBotResponseDto casperBot,
                                            LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static LotteryParticipantResponseDto of(LotteryParticipants lotteryParticipants, CasperBotResponseDto casperBotResponseDto) {
        return new LotteryParticipantResponseDto(
                lotteryParticipants.getLinkClickedCount(),
                lotteryParticipants.getExpectations(),
                lotteryParticipants.getAppliedCount(),
                casperBotResponseDto,
                lotteryParticipants.getBaseUser().getCreatedAt(),
                lotteryParticipants.getBaseUser().getUpdatedAt()
        );
    }
}