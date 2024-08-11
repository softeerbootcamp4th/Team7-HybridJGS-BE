package JGS.CasperEvent.domain.event.entity.event;

import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
public class RushEvent extends BaseEvent {
    private String prizeImageUrl;
    private String prizeDescription;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rushEventId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "rush_event_id")
    private Set<RushOption> options;

    @OneToMany(mappedBy = "rushEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RushParticipants> rushParticipants;

    public RushEvent() {
    }

    public RushEvent(String prizeImageUrl, String prizeDescription) {
        super();
        this.prizeImageUrl = prizeImageUrl;
        this.prizeDescription = prizeDescription;
    }


    public RushEvent(LocalDateTime startDateTime, LocalDateTime endDateTime, int winnerCount, String prizeImageUrl, String prizeDescription) {
        super(startDateTime, endDateTime, winnerCount);
        this.prizeImageUrl = prizeImageUrl;
        this.prizeDescription = prizeDescription;
    }
}
