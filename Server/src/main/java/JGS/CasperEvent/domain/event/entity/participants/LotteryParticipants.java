package JGS.CasperEvent.domain.event.entity.participants;

import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.Role;
import jakarta.persistence.Entity;
import lombok.Getter;

@Getter
@Entity
public class LotteryParticipants extends BaseUser {
    private int linkClickedCount;
    private int expectations;
    private int appliedCount;

    public String getPhoneNumber() {
        return getId();
    }

    public LotteryParticipants() {

    }

    public void expectationAdded(){
        expectations++;
    }

    public LotteryParticipants(String phoneNumber) {
        super(phoneNumber, Role.USER);
        this.appliedCount = 1;
        this.linkClickedCount = 0;
        this.expectations = 0;
    }
}
