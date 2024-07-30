package JGS.CasperEvent.domain.event.entity.participants;

import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class LotteryParticipants extends BaseEntity {
    private int linkClickedCount;
    private int expectations;

    @ManyToOne
    @JoinColumn(name="lottery_event_id")
    private LotteryEvent lotteryEvent;

    @Id
    private String phoneNumber;
}
