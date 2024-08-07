package JGS.CasperEvent.domain.event.repository.participantsRepository;

import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RushParticipantsRepository extends JpaRepository<RushParticipants, String> {
    @Query("SELECT CASE WHEN COUNT(rp) > 0 THEN TRUE ELSE FALSE END " +
            "FROM RushParticipants rp " +
            "WHERE rp.rushEvent.rushEventId = :eventId AND rp.baseUser.id = :userId")
    boolean existsByRushEventIdAndUserId(@Param("eventId") Long eventId, @Param("userId") String userId);

    @Query("SELECT COUNT(rp) FROM RushParticipants rp " +
            "WHERE rp.rushEvent.rushEventId = :eventId AND rp.optionId = :optionId")
    long countByRushEventIdAndOptionId(@Param("eventId") Long eventId, @Param("optionId") int optionId);
}
