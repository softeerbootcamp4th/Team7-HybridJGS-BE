package JGS.CasperEvent.domain.event.entity.event;

import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class LotteryEvent extends BaseEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lotteryEventId;

    @OneToMany(mappedBy = "lotteryEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LotteryParticipants> lotteryParticipants;

    public Long getLotteryEventId() {
        return lotteryEventId;
    }
}
