package JGS.CasperEvent.domain.event.dto.ResponseDto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RushEventListResponseDto {
    private List<MainRushEventResponseDto> events;
    private LocalDateTime serverTime;
    private Long todayEventId;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
    private Long activePeriod;

    public RushEventListResponseDto(List<MainRushEventResponseDto> events, LocalDateTime serverTime, Long todayEventId, LocalDate eventStartDate, LocalDate eventEndDate, Long activePeriod) {
        this.events = events;
        this.serverTime = serverTime;
        this.todayEventId = todayEventId;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.activePeriod = activePeriod;
    }
}
