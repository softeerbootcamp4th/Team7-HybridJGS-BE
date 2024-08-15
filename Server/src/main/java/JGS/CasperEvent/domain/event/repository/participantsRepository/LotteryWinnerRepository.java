package JGS.CasperEvent.domain.event.repository.participantsRepository;

import JGS.CasperEvent.domain.event.entity.participants.LotteryWinners;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LotteryWinnerRepository extends JpaRepository<LotteryWinners, Long> {
    @Query("SELECT w FROM LotteryWinners w WHERE w.phoneNumber LIKE :phoneNumber%")
    Page<LotteryWinners> findByPhoneNumber(@Param("phoneNumber") String phoneNumber, Pageable pageable);

    @Query("SELECT COUNT(w) FROM LotteryWinners w WHERE w.phoneNumber LIKE :phoneNumber%")
    long countByPhoneNumber(@Param("phoneNumber") String phoneNumber);}
