package JGS.CasperEvent.global.entity;

import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.global.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class BaseUser extends BaseEntity {
    @Id
    String id;
    Role role;

    @JsonManagedReference
    @OneToOne(mappedBy = "baseUser", cascade = CascadeType.ALL)
    private LotteryParticipants lotteryParticipants;

    public void updateLotteryParticipants(LotteryParticipants lotteryParticipant) {
        this.lotteryParticipants = lotteryParticipant;
    }

    public BaseUser(String id, Role role) {
        this.id = id;
        this.role = role;
    }

    public BaseUser() {
    }
}
