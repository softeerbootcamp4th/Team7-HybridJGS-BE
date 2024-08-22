package JGS.CasperEvent.global.jwt.repository;

import JGS.CasperEvent.global.entity.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<BaseUser, Long> {
    Optional<BaseUser> findByPhoneNumber(String phoneNumber);
}
