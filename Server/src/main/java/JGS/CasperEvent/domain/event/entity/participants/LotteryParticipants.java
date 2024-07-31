package JGS.CasperEvent.domain.event.entity.participants;

import JGS.CasperEvent.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class LotteryParticipants extends BaseEntity {
    private int linkClickedCount;
    private int expectations;
    private int appliedCount;

    @Id
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LotteryParticipants() {

    }

    public void expectationAdded(){
        expectations++;
    }

    public LotteryParticipants(String phoneNumber){
        this.phoneNumber = phoneNumber;
        this.appliedCount = 1;
        this.linkClickedCount = 0;
        this.expectations = 0;
    }
}
