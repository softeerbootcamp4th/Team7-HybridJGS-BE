package JGS.CasperEvent.domain.event.repository.participantsRepository;

import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RushParticipantsRepository extends JpaRepository<RushParticipants, String> {
    boolean existsByRushEvent_RushEventIdAndBaseUser_Id(Long eventId, String userId);
    long countByRushEvent_RushEventIdAndOptionId(Long eventId, int optionId);
    @Query("SELECT COUNT(rp) + 1 FROM RushParticipants rp " +
            "WHERE rp.rushEvent.rushEventId = :eventId " +
            "AND rp.optionId = :optionId " +
            "AND rp.id < (SELECT rp2.id FROM RushParticipants rp2 " +
            "WHERE rp2.rushEvent.rushEventId = :eventId " +
            "AND rp2.baseUser.id = :userId)")
    long findUserRankByEventIdAndUserIdAndOptionId(@Param("eventId") Long eventId,
                                                   @Param("userId") String userId,
                                                   @Param("optionId") int optionId);
    long countAllByOptionId(int optionId);
    @Query("SELECT rp.optionId FROM RushParticipants rp WHERE rp.baseUser.id = :userId")
    Optional<Integer> getOptionIdByUserId(@Param("userId") String userId);

}
