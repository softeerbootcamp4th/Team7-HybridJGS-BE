package JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto;

import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;

import java.time.LocalDate;
import java.time.LocalTime;

public record LotteryEventParticipantsResponseDto(
        Long id, String phoneNumber, int linkClickedCounts,
        int expectation, int appliedCount,
        LocalDate createdDate, LocalTime createdTime) {

    public static LotteryEventParticipantsResponseDto of(LotteryParticipants participant) {
        return new LotteryEventParticipantsResponseDto(
                participant.getId(),
                participant.getBaseUser().getPhoneNumber(),
                participant.getLinkClickedCount(),
                participant.getExpectations(),
                participant.getAppliedCount(),
                participant.getBaseUser().getCreatedAt().toLocalDate(),
                participant.getBaseUser().getCreatedAt().toLocalTime()
        );
    }

}
