package JGS.CasperEvent.domain.event.entity.event;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class LotteryEvent extends BaseEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lotteryEventId;

    public LotteryEvent(LocalDateTime eventStartDateTime, LocalDateTime eventEndDateTime, int winnerCount){
        this.startDateTime = eventStartDateTime;
        this.endDateTime = eventEndDateTime;
        this.winnerCount = winnerCount;
    }

    public LotteryEvent() {
    }
}
