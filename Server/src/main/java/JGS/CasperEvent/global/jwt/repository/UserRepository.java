package JGS.CasperEvent.global.jwt.repository;

import JGS.CasperEvent.global.entity.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<BaseUser, String> {
}
