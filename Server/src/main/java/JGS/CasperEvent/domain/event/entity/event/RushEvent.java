package JGS.CasperEvent.domain.event.entity.event;

import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
public class RushEvent extends BaseEvent {
    private String prizeImageUrl;
    private String prizeDescription;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rushEventId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "rushEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final Set<RushOption> options = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "rushEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RushParticipants> rushParticipants;

    public RushEvent() {
    }

    public RushEvent(String prizeImageUrl, String prizeDescription) {
        super();
        this.prizeImageUrl = prizeImageUrl;
        this.prizeDescription = prizeDescription;
    }


    public RushEvent(LocalDateTime startDateTime, LocalDateTime endDateTime,
                     int winnerCount, String prizeImageUrl, String prizeDescription) {
        super(startDateTime, endDateTime, winnerCount);
        this.prizeImageUrl = prizeImageUrl;
        this.prizeDescription = prizeDescription;
    }

    public void addOption(RushOption leftOption, RushOption rightOption){
        this.options.add(leftOption);
        this.options.add(rightOption);
    }
}
