package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;

import java.time.LocalDateTime;

public record RushEventParticipantResponseDto(Long id, String phoneNumber,
                                              int balanceGameChoice, LocalDateTime createdAt, LocalDateTime updatedAt,
                                              Long rank) {
    public static RushEventParticipantResponseDto of(RushParticipants rushParticipants, Long rank){
        return new RushEventParticipantResponseDto(
                rushParticipants.getId(),
                rushParticipants.getBaseUser().getId(),
                rushParticipants.getOptionId(),
                rushParticipants.getCreatedAt(),
                rushParticipants.getUpdatedAt(),
                rank
        );
    }
}
