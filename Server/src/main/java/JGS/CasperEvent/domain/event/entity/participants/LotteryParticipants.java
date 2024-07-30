package JGS.CasperEvent.domain.event.entity.participants;

import JGS.CasperEvent.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class LotteryParticipants extends BaseEntity {
    private int linkClickedCount;
    private int expectations;

    @Id
    private String phoneNumber;

    public LotteryParticipants() {

    }

    public LotteryParticipants(String phoneNumber){
        this.phoneNumber = phoneNumber;
        this.linkClickedCount = 0;
        this.expectations = 0;
    }
}
