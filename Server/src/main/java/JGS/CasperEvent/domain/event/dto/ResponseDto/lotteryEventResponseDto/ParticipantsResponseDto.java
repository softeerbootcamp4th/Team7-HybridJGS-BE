package JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto;

import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;

import java.time.LocalDateTime;

public record ParticipantsResponseDto(
        Long id, String phoneNumber, int linkClickedCounts,
        int expectation, int appliedCount,
        LocalDateTime createdAt, LocalDateTime updatedAt) {

    public static ParticipantsResponseDto of(LotteryParticipants participant) {
        return new ParticipantsResponseDto(
                participant.getId(),
                participant.getBaseUser().getId(),
                participant.getLinkClickedCount(),
                participant.getExpectations(),
                participant.getAppliedCount(),
                participant.getBaseUser().getCreatedAt(),
                participant.getBaseUser().getUpdatedAt()
        );
    }
}
