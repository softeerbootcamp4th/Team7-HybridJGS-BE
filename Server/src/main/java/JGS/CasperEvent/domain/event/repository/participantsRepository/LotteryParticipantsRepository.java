package JGS.CasperEvent.domain.event.repository.participantsRepository;

import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.global.entity.BaseUser;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LotteryParticipantsRepository extends JpaRepository<LotteryParticipants, Long> {
    Optional<LotteryParticipants> findByBaseUser(BaseUser baseUser);

    @Query("SELECT p FROM LotteryParticipants p WHERE p.baseUser.id LIKE %:id%")
    Page<LotteryParticipants> findByBaseUser_Id(@Param("id") String id, Pageable pageable);

    @Query("SELECT COUNT(p) FROM LotteryParticipants p WHERE p.baseUser.id LIKE %:id%")
    long countByBaseUser_Id(@Param("id") String id);
}

