package JGS.CasperEvent.domain.event.entity.event;

import JGS.CasperEvent.global.entity.BaseEntity;

import java.time.LocalDate;

public class BaseEvent extends BaseEntity {
    protected LocalDate eventStartDate;
    protected LocalDate eventEndDate;
    protected int winnerCount;

    public LocalDate getEventStartDate() {
        return eventStartDate;
    }

    public LocalDate getEventEndDate() {
        return eventEndDate;
    }

    public int getWinnerCount() {
        return winnerCount;
    }
}
