package JGS.CasperEvent.domain.event.entity.event;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@EqualsAndHashCode
public class LotteryEvent extends BaseEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lotteryEventId;

    public LotteryEvent(LocalDateTime startDateTime, LocalDateTime endDateTime, int winnerCount) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.winnerCount = winnerCount;
    }

    public LotteryEvent() {
    }

    public LotteryEvent updateLotteryEvent(LocalDateTime startDateTime, LocalDateTime endDateTime, int winnerCount) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.winnerCount = winnerCount;
        return this;
    }
}
