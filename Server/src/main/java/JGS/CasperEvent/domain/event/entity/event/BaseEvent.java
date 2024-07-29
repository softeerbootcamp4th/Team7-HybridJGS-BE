package JGS.CasperEvent.domain.event.entity.event;

import JGS.CasperEvent.global.entity.BaseEntity;

import java.time.LocalDate;

public class BaseEvent extends BaseEntity {
    private int eventId;
    private LocalDate start_date;
    private LocalDate end_date;
    private int winnerCount;
}
