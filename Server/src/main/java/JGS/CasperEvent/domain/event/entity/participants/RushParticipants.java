package JGS.CasperEvent.domain.event.entity.participants;

import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class RushParticipants extends BaseEntity {
    private int choice;

    @ManyToOne
    @JoinColumn(name = "rush_event_id")
    private RushEvent rushEvent;

    @Id
    private String phoneNumber;
}
