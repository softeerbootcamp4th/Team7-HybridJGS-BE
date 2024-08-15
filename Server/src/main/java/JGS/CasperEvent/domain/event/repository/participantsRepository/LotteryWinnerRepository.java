package JGS.CasperEvent.domain.event.repository.participantsRepository;

import JGS.CasperEvent.domain.event.entity.participants.LotteryWinners;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotteryWinnerRepository extends JpaRepository<LotteryWinners, Long> {
    Page<LotteryWinners> findByPhoneNumber(String phoneNumber, Pageable pageable);
}
