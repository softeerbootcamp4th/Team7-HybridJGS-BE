package JGS.CasperEvent.domain.event.repository;

import JGS.CasperEvent.domain.event.entity.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {
}
