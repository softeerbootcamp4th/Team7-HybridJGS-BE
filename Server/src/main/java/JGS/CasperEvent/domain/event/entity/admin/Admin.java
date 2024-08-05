package JGS.CasperEvent.domain.event.entity.admin;

import JGS.CasperEvent.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Admin extends BaseEntity {
    @Id
    private String adminId;
    private String password;

    public Admin(String adminId, String password) {
        this.adminId = adminId;
        this.password = password;
    }

    public Admin() {

    }
}
