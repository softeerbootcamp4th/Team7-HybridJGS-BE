package JGS.CasperEvent.domain.event.entity.participants;

import JGS.CasperEvent.global.entity.BaseUser;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
    //todo: 왜이런지 알아보기
    @JsonBackReference
    private BaseUser baseUser;

    private int linkClickedCount;
    private int expectations;
    private int appliedCount;

    private Long casperId;

    public void updateCasperId(Long casperId) {
        this.casperId = casperId;
    }

    public LotteryParticipants() {

    }

    public void expectationAdded() {
        expectations++;
        appliedCount = Math.max(10, appliedCount + 1);
    }

    public void linkClickedCountAdded() {
        linkClickedCount++;
        appliedCount = Math.max(10, appliedCount + 1);
    }

    public LotteryParticipants(BaseUser baseUser) {
        this.baseUser = baseUser;
        this.appliedCount = 1;
        this.linkClickedCount = 0;
        this.expectations = 0;
    }
}
