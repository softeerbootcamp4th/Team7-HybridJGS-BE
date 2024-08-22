package JGS.CasperEvent.global.entity;

import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import JGS.CasperEvent.global.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@Inheritance(strategy = InheritanceType.JOINED)
public class BaseUser extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String phoneNumber;
    Role role;

    @JsonManagedReference
    @OneToMany(mappedBy = "baseUser", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<LotteryParticipants> lotteryParticipants;

    @JsonManagedReference
    @OneToMany(mappedBy = "baseUser", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<RushParticipants> rushParticipants;

    public BaseUser(String phoneNumber, Role role) {
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public BaseUser() {
    }
}
