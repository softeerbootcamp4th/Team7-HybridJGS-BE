package JGS.CasperEvent.global.entity;

import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.global.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class BaseUser extends BaseEntity {
    @Id
    String id;
    Role role;

    @OneToOne(mappedBy = "baseUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LotteryParticipants lotteryParticipants;

    public BaseUser(String id, Role role) {
        this.id = id;
        this.role = role;
    }

    public BaseUser() {
    }
}
