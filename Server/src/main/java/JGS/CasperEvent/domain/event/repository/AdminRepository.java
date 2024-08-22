package JGS.CasperEvent.domain.event.repository;

import JGS.CasperEvent.domain.event.entity.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {
    Optional<Admin> findByPhoneNumberAndPassword(String id, String password);
    Optional<Admin> findByPhoneNumber(String id);
}
