package JGS.CasperEvent.global.enums;

import lombok.Getter;

@Getter
public enum EventStatus {
    BEFORE(1),
    DURING(2),
    AFTER(3);

    private final int eventStatus;

    EventStatus(int eventStatus){
        this.eventStatus = eventStatus;
    }
}
