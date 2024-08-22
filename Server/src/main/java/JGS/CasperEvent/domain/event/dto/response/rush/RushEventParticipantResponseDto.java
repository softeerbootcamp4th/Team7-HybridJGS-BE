package JGS.CasperEvent.domain.event.dto.response.rush;

import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class RushEventParticipantResponseDto {
    private Long id;
    private String phoneNumber;
    private LocalDate createdDate;
    private LocalTime createdTime;
    private int balanceGameChoice;
    private Long rank;


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