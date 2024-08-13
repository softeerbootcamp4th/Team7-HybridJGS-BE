package JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto;

import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.global.enums.EventStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record LotteryEventDetailResponseDto(
        LocalDate startDate, LocalTime startTime,
        LocalDate endDate, LocalTime endTime,
        int appliedCount, int winnerCount,
        EventStatus status,
        LocalDateTime createdAt, LocalDateTime updatedAt) {

    public static LotteryEventDetailResponseDto of(LotteryEvent event) {

        EventStatus status;
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(event.getStartDateTime())) status = EventStatus.BEFORE;
        else if (now.isAfter(event.getEndDateTime())) status = EventStatus.AFTER;
        else status = EventStatus.DURING;

        return new LotteryEventDetailResponseDto(
                event.getStartDateTime().toLocalDate(),
                event.getStartDateTime().toLocalTime(),
                event.getEndDateTime().toLocalDate(),
                event.getEndDateTime().toLocalTime(),
                event.getTotalAppliedCount(),
                event.getWinnerCount(),
                status,
                event.getCreatedAt(),
                event.getUpdatedAt());
    }
}
