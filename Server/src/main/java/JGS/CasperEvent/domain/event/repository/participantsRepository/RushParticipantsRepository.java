package JGS.CasperEvent.domain.event.repository.participantsRepository;

import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RushParticipantsRepository extends JpaRepository<RushParticipants, Long> {
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

    Page<RushParticipants> findByRushEvent_RushEventId(Long rushEventId, Pageable pageable);

    Page<RushParticipants> findByRushEvent_RushEventIdAndBaseUser_Id(Long rushEventId, String baseUser_id, Pageable pageable);

    Page<RushParticipants> findByRushEvent_RushEventIdAndOptionId(Long rushEventId, int optionId, Pageable pageable);

    Page<RushParticipants> findByRushEvent_RushEventIdAndOptionIdAndBaseUser_Id(Long rushEventId, int optionId, String baseUser_id, Pageable pageable);


    @Query("SELECT rp FROM RushParticipants rp " +
            "WHERE rp.rushEvent.rushEventId = :eventId " +
            "AND rp.optionId = :optionId " +
            "ORDER BY rp.id ASC ")
    Page<RushParticipants> findWinnerByEventIdAndOptionId(
            @Param("eventId") Long eventId, @Param("optionId") int optionId, Pageable pageable);

    @Query("SELECT rp FROM RushParticipants rp " +
            "WHERE rp.rushEvent.rushEventId = :eventId " +
            "AND rp.optionId = :optionId " +
            "AND rp.baseUser.id = :phoneNumber " +
            "ORDER BY rp.id ASC ")
    Page<RushParticipants> findWinnerByEventIdAndOptionIdAndPhoneNumber(
            @Param("eventId") Long eventId, @Param("optionId") int optionId, @Param("phoneNumber") String phoneNumber, Pageable pageable
    );

    @Query("SELECT rp FROM RushParticipants rp " +
            "WHERE rp.rushEvent.rushEventId = :eventId " +
            "ORDER BY rp.id ASC ")
    Page<RushParticipants> findWinnerByEventId(@Param("eventId") Long eventId, @Param("winnerCount") Pageable pageable);


    @Query("SELECT rp FROM RushParticipants rp " +
            "WHERE rp.rushEvent.rushEventId = :eventId " +
            "AND rp.baseUser.id = :phoneNumber " +
            "ORDER BY rp.id ASC ")
    Page<RushParticipants> findByWinnerByEventIdAndPhoneNumber(@Param("eventId") Long eventId, @Param("phoneNumber") String phoneNumber, Pageable pageable);
}
