package JGS.CasperEvent.domain.event.entity.admin;

import JGS.CasperEvent.global.entity.BaseEntity;
import jakarta.persistence.Id;

public class Admin extends BaseEntity {
    @Id
    private String adminId;
    private String password;
}
