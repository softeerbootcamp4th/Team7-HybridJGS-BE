package JGS.CasperEvent.domain.event.repository.participantsRepository;

import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.global.entity.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LotteryParticipantsRepository extends JpaRepository<LotteryParticipants, String> {
    Optional<LotteryParticipants> findByBaseUser(BaseUser baseUser);
}

