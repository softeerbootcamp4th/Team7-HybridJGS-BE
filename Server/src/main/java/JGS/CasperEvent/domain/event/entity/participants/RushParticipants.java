package JGS.CasperEvent.domain.event.entity.participants;

import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.global.entity.BaseEntity;
import JGS.CasperEvent.global.entity.BaseUser;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class RushParticipants extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_user_id")
    @JsonBackReference
    private BaseUser baseUser;

    @ManyToOne(fetch = FetchType.LAZY)
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
