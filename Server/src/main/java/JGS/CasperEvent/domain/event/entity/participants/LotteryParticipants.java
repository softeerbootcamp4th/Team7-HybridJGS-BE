package JGS.CasperEvent.domain.event.entity.participants;

import JGS.CasperEvent.global.entity.BaseEntity;
import JGS.CasperEvent.global.entity.BaseUser;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class LotteryParticipants extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne // mappedBy 이용하면 둘 다 저장 안해도 됨
    @JoinColumn(name = "base_user_id")
    //todo: 왜이런지 알아보기
    @JsonBackReference
    @JsonIgnore
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
        if (expectations == 0) expectations++;
        appliedCount = Math.min(10, 1 + expectations + linkClickedCount);
    }

    public void linkClickedCountAdded() {
        linkClickedCount++;
        appliedCount = Math.min(10, 1 + expectations + linkClickedCount);
    }

    public LotteryParticipants(BaseUser baseUser) {
        this.baseUser = baseUser;
        this.appliedCount = 1;
        this.linkClickedCount = 0;
        this.expectations = 0;
    }
}
