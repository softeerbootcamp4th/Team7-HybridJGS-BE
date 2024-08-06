package JGS.CasperEvent.domain.event.entity.participants;

import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.global.entity.BaseUser;
import jakarta.persistence.*;

@Entity
public class RushParticipants {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int optionId;
    @OneToOne
    @JoinColumn(name = "base_user_id")
    private BaseUser baseUser;

    @ManyToOne
    @JoinColumn(name = "rush_event_id")
    private RushEvent rushEvent;

    public RushParticipants(BaseUser baseUser, RushEvent rushEvent, int optionId) {
        this.baseUser = baseUser;
        this.rushEvent = rushEvent;
        this.optionId = optionId;
    }

    public RushParticipants() {

    }
}
