package JGS.CasperEvent.domain.event.dto.response.lottery;

import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.global.enums.EventStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class LotteryEventResponseDto {
    private LocalDateTime serverDateTime;
    private LocalDateTime eventStartDate;
    private LocalDateTime eventEndDate;

    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;

    private int winnerCount;
    private EventStatus status;


    private int appliedCount;
    private Long activePeriod;

    private Long casperId;
    private String expectation;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDate createdDate;
    private LocalTime createdTime;

    private LotteryEventResponseDto(LocalDateTime serverDateTime,
                                    LocalDateTime eventStartDate,
                                    LocalDateTime eventEndDate) {
        this.serverDateTime = serverDateTime;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.activePeriod = ChronoUnit.DAYS.between(eventStartDate, eventEndDate);
    }

    public static LotteryEventResponseDto of(LotteryEvent lotteryEvent, LocalDateTime serverDateTime) {
        return new LotteryEventResponseDto(
                serverDateTime,
                lotteryEvent.getStartDateTime(),
                lotteryEvent.getEndDateTime()
        );
    }

    private LotteryEventResponseDto(LocalDate startDate, LocalTime startTime,
                                    LocalDate endDate, LocalTime endTime,
                                    int appliedCount, int winnerCount,
                                    EventStatus status,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.appliedCount = appliedCount;
        this.winnerCount = winnerCount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LotteryEventResponseDto withDetail(LotteryEvent event) {
        EventStatus status;
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(event.getStartDateTime())) status = EventStatus.BEFORE;
        else if (now.isAfter(event.getEndDateTime())) status = EventStatus.AFTER;
        else status = EventStatus.DURING;

        return new LotteryEventResponseDto(
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

    private LotteryEventResponseDto(Long casperId, String expectation,
                                    LocalDate createdDate, LocalTime createdTime) {
        this.casperId = casperId;
        this.expectation = expectation;
        this.createdDate = createdDate;
        this.createdTime = createdTime;
    }

    public static LotteryEventResponseDto withExpectation(CasperBot casperBot) {
        return new LotteryEventResponseDto(
                casperBot.getCasperId(),
                casperBot.getExpectation(),
                casperBot.getCreatedAt().toLocalDate(),
                casperBot.getCreatedAt().toLocalTime()
        );
    }
}