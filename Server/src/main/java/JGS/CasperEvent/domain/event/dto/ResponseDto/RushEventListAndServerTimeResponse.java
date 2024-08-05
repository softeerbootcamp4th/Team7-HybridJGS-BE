package JGS.CasperEvent.domain.event.dto.ResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public class RushEventListAndServerTimeResponse {
    private List<GetRushEvent> events;
    private LocalDateTime serverTime;

    public RushEventListAndServerTimeResponse(List<GetRushEvent> events, LocalDateTime serverTime) {
        this.events = events;
        this.serverTime = serverTime;
    }

    // Getters and setters
    public List<GetRushEvent> getEvents() {
        return events;
    }
    public void setEvents(List<GetRushEvent> events) {
        this.events = events;
    }

    public LocalDateTime getServerTime() {
        return serverTime;
    }

    public void setServerTime(LocalDateTime serverTime) {
        this.serverTime = serverTime;
    }

}
