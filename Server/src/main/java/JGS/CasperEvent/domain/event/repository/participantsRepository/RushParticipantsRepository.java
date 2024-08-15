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

    @Query("SELECT p FROM RushParticipants p WHERE p.rushEvent.rushEventId = :rushEventId AND p.baseUser.id LIKE :baseUserId%")
    Page<RushParticipants> findByRushEvent_RushEventIdAndBaseUser_Id(@Param("rushEventId") Long rushEventId, @Param("baseUserId") String baseUserId, Pageable pageable);

    Page<RushParticipants> findByRushEvent_RushEventIdAndOptionId(Long rushEventId, int optionId, Pageable pageable);

    @Query("SELECT p FROM RushParticipants p WHERE p.rushEvent.rushEventId = :rushEventId AND p.optionId = :optionId AND p.baseUser.id LIKE :baseUserId%")
    Page<RushParticipants> findByRushEvent_RushEventIdAndOptionIdAndBaseUser_Id(@Param("rushEventId") Long rushEventId, @Param("optionId") int optionId, @Param("baseUserId") String baseUserId, Pageable pageable);


    @Query("SELECT rp FROM RushParticipants rp " +
            "WHERE rp.rushEvent.rushEventId = :eventId " +
            "AND rp.optionId = :optionId " +
            "ORDER BY rp.id ASC ")
    Page<RushParticipants> findWinnerByEventIdAndOptionId(
            @Param("eventId") Long eventId, @Param("optionId") int optionId, Pageable pageable);

    @Query("SELECT rp FROM RushParticipants rp " +
            "WHERE rp.rushEvent.rushEventId = :eventId " +
            "AND rp.optionId = :optionId " +
            "AND rp.baseUser.id LIKE :phoneNumber% " +
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
            "AND rp.baseUser.id LIKE :phoneNumber% " +
            "ORDER BY rp.id ASC ")
    Page<RushParticipants> findByWinnerByEventIdAndPhoneNumber(@Param("eventId") Long eventId, @Param("phoneNumber") String phoneNumber, Pageable pageable);

    @Query("SELECT COUNT(p) FROM RushParticipants p WHERE p.rushEvent.rushEventId = :rushEventId AND p.optionId = :optionId AND p.baseUser.id LIKE :baseUserId%")
    long countByRushEvent_RushEventIdAndOptionIdAndBaseUser_Id(@Param("rushEventId") Long rushEventId, @Param("optionId") int optionId, @Param("baseUserId") String baseUserId);

    long countByRushEvent_RushEventId(long rushEventId);

    @Query("SELECT COUNT(p) FROM RushParticipants p WHERE p.rushEvent.rushEventId = :rushEventId AND p.baseUser.id LIKE :baseUserId%")
    long countByRushEvent_RushEventIdAndBaseUser_Id(@Param("rushEventId") Long rushEventId, @Param("baseUserId") String baseUserId);
}