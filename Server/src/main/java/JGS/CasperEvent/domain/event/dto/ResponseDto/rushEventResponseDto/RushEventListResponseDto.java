package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import JGS.CasperEvent.domain.event.dto.response.rush.RushEventResponseDto;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RushEventListResponseDto {
    private List<RushEventResponseDto> events;
    private LocalDateTime serverTime;
    private Long todayEventId;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;
    private Long activePeriod;

    public RushEventListResponseDto(List<RushEventResponseDto> events, LocalDateTime serverTime, Long todayEventId, LocalDate eventStartDate, LocalDate eventEndDate, Long activePeriod) {
        this.events = events;
        this.serverTime = serverTime;
        this.todayEventId = todayEventId;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.activePeriod = activePeriod;
    }
}
