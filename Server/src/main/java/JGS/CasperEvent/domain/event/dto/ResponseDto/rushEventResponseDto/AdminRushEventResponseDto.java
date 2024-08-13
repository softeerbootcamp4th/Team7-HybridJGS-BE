package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.global.enums.EventStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record AdminRushEventResponseDto(Long rushEventId, LocalDate eventDate,
                                        LocalTime startTime, LocalTime endTime,
                                        int winnerCount, String prizeImageUrl,
                                        String prizeDescription, LocalDateTime createdAt, LocalDateTime updatedAt,
                                        EventStatus status, Set<RushEventOptionResponseDto> options) {

    public static List<AdminRushEventResponseDto> of(List<RushEvent> rushEvents) {
        List<AdminRushEventResponseDto> dtoList = new ArrayList<>();
        for (RushEvent rushEvent : rushEvents) {
            Set<RushEventOptionResponseDto> options = rushEvent.getOptions().stream()
                    .map(RushEventOptionResponseDto::of)
                    .collect(Collectors.toSet());

            LocalDateTime now = LocalDateTime.now();
            EventStatus status;
            if (now.isBefore(rushEvent.getStartDateTime())) status = EventStatus.BEFORE;
            else if (now.isAfter(rushEvent.getEndDateTime())) status = EventStatus.AFTER;
            else status = EventStatus.DURING;

            dtoList.add(new AdminRushEventResponseDto(
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
            ));
        }

        return dtoList;
    }
}
