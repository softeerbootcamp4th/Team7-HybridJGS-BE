package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import JGS.CasperEvent.domain.event.dto.response.rush.RushEventOptionResponseDto;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.global.enums.EventStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

public record AdminRushEventResponseDto(Long rushEventId, LocalDate eventDate,
                                        LocalTime startTime, LocalTime endTime,
                                        int winnerCount, String prizeImageUrl,
                                        String prizeDescription, LocalDateTime createdAt, LocalDateTime updatedAt,
                                        EventStatus status, Set<RushEventOptionResponseDto> options) {
    public static AdminRushEventResponseDto of(RushEvent rushEvent){
        Set<RushEventOptionResponseDto> options = rushEvent.getOptions().stream()
                .map(RushEventOptionResponseDto::of)
                .collect(Collectors.toSet());

        LocalDateTime now = LocalDateTime.now();
        EventStatus status;
        if (now.isBefore(rushEvent.getStartDateTime())) status = EventStatus.BEFORE;
        else if (now.isAfter(rushEvent.getEndDateTime())) status = EventStatus.AFTER;
        else status = EventStatus.DURING;

        return new AdminRushEventResponseDto(
                rushEvent.getRushEventId(),
                rushEvent.getStartDateTime().toLocalDate(),
                rushEvent.getStartDateTime().toLocalTime(),
                rushEvent.getEndDateTime().toLocalTime(),
                rushEvent.getWinnerCount(),
                rushEvent.getPrizeImageUrl(),
                rushEvent.getPrizeDescription(),
                rushEvent.getCreatedAt(),
                rushEvent.getUpdatedAt(),
                status,
                options
        );
    }
}
