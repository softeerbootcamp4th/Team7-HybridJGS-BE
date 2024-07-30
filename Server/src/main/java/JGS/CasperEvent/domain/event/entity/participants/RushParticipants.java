package JGS.CasperEvent.domain.event.entity.participants;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class RushParticipants extends BaseParticipant{
    private int choice;

    @ManyToOne
    @JoinColumn(name = "rush_event_id")
    private Long rushEventId;
}
