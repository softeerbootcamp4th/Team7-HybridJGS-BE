package JGS.CasperEvent.global.entity;

import JGS.CasperEvent.global.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class BaseUser extends BaseEntity {
    @Id
    String id;
    Role role;

    public BaseUser(String id, Role role) {
        this.id = id;
        this.role = role;
    }

    public BaseUser() {
    }
}
