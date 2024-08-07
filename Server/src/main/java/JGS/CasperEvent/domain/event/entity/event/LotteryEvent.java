package JGS.CasperEvent.domain.event.entity.event;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class LotteryEvent extends BaseEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lotteryEventId;
}
