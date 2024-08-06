package JGS.CasperEvent.domain.event.entity.participants;

import JGS.CasperEvent.global.entity.BaseUser;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class LotteryParticipants {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "base_user_id")
    private BaseUser baseUser;

    private int linkClickedCount;
    private int expectations;
    private int appliedCount;

    private Long casperId;

    public void updateCasperId(Long casperId){
        this.casperId = casperId;
    }

    public LotteryParticipants() {

    }

    public void expectationAdded(){
        expectations++;
    }

    public LotteryParticipants(BaseUser baseUser) {
        this.baseUser = baseUser;
        this.appliedCount = 1;
        this.linkClickedCount = 0;
        this.expectations = 0;
    }
}
