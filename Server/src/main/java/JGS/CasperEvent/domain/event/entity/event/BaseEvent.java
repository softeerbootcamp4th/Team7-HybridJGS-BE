package JGS.CasperEvent.domain.event.entity.event;

import JGS.CasperEvent.global.entity.BaseEntity;

import java.time.LocalDate;

public class BaseEvent extends BaseEntity {
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected int winnerCount;

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getWinnerCount() {
        return winnerCount;
    }
}
