package JGS.CasperEvent.domain.event.entity.participants;

import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class RushParticipants extends BaseUser {
    private int choice;
    @ManyToOne
    @JoinColumn(name = "rush_event_id")
    private RushEvent rushEvent;

    public RushParticipants(String id, Role role) {
        super(id, role);
    }

    public RushParticipants() {
        super();
    }
}
