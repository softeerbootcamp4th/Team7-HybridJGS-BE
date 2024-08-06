package JGS.CasperEvent.domain.event.entity.admin;

import JGS.CasperEvent.global.entity.BaseEntity;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.Role;
import jakarta.persistence.Entity;

@Entity
public class Admin extends BaseUser {
    private String password;

    public Admin(String id, String password, Role role) {
        super(id, role);
        this.password = password;
    }

    public Admin() {}
}
