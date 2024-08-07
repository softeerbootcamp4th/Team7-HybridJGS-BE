package JGS.CasperEvent.domain.event.dto.ResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public class RushEventListAndServerTimeResponseDto {
    private List<RushEventResponseDto> events;
    private LocalDateTime serverTime;

    public RushEventListAndServerTimeResponseDto(List<RushEventResponseDto> events, LocalDateTime serverTime) {
        this.events = events;
        this.serverTime = serverTime;
    }

    // Getters and setters
    public List<RushEventResponseDto> getEvents() {
        return events;
    }
    public void setEvents(List<RushEventResponseDto> events) {
        this.events = events;
    }

    public LocalDateTime getServerTime() {
        return serverTime;
    }

    public void setServerTime(LocalDateTime serverTime) {
        this.serverTime = serverTime;
    }

}
