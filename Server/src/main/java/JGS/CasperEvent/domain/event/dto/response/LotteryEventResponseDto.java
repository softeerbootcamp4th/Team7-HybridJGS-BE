package JGS.CasperEvent.domain.event.dto.response;

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
    private final LocalDateTime serverDateTime;
    private final LocalDateTime eventStartDate;
    private final LocalDateTime eventEndDate;

    //    private LocalDate eventStartDate;
    private LocalTime eventStartTime;
    //    private LocalDate eventEndDate;
    private LocalTime eventEndTime;

    private int winnerCount;
    private EventStatus status;


    private int appliedCount;
    private final Long activePeriod;

    private Long casperId;
    private String expectation;

    private LocalDate createdDate;
    private LocalTime createdTime;
    private LocalDate updatedDate;
    private LocalTime updatedTime;

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
}