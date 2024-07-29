package JGS.CasperEvent.domain.event.entity.participants;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class LotteryParticipants extends BaseParticipant{
    private int linkClickedCount;
    private int expectations;

    @ManyToOne
    @JoinColumn(name="lottery_event_id")
    private Long lotteryEventId;
}
