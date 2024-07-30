package JGS.CasperEvent.domain.event.entity.event;

import jakarta.persistence.*;

@Entity
public class LotteryEvent extends BaseEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lotteryEventId;

    public Long getLotteryEventId() {
        return lotteryEventId;
    }
}
