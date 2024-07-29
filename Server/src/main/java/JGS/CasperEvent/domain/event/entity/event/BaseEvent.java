package JGS.CasperEvent.domain.event.entity.event;

import JGS.CasperEvent.global.entity.BaseEntity;

import java.time.LocalDate;

public class BaseEvent extends BaseEntity {
    private LocalDate startDate;
    private LocalDate endDate;
    private int winnerCount;

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
