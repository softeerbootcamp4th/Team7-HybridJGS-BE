package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record RushEventParticipantResponseDto(Long id, String phoneNumber,
                                              int balanceGameChoice, LocalDate createdDate, LocalTime createTime,
                                              Long rank) {
    public static RushEventParticipantResponseDto of(RushParticipants rushParticipants, Long rank){
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
