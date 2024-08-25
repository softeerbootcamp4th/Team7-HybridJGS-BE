package JGS.CasperEvent.global.enums;

import lombok.Getter;

@Getter
public enum EventStatus {
    BEFORE(1),
    DURING(2),
    AFTER(3);

    private final int statusNum;

    EventStatus(int statusNum){
        this.statusNum = statusNum;
    }
}
