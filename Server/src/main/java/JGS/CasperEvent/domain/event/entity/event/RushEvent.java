package JGS.CasperEvent.domain.event.entity.event;

import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class RushEvent extends BaseEvent {
    private String prizeImageUrl;
    private String prizeDescription;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rushEventId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "rush_event_id")
    private RushOption leftOption;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "rush_event_id")
    private RushOption rightOption;

    @OneToMany(mappedBy = "rushEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RushParticipants> rushParticipants;

    public RushEvent() {
    }

    // 파라미터가 있는 생성자
    public RushEvent(String prizeImageUrl, String prizeDescription) {
        this.prizeImageUrl = prizeImageUrl;
        this.prizeDescription = prizeDescription;
    }

    public String getPrizeImageUrl() {
        return prizeImageUrl;
    }

    public String getPrizeDescription() {
        return prizeDescription;
    }

    public RushOption getLeftOption() {
        return leftOption;
    }

    public RushOption getRightOption() {
        return rightOption;
    }

    public Long getRushEventId() {
        return rushEventId;
    }
}
