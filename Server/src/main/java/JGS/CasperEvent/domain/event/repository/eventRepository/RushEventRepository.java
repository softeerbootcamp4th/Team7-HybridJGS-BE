package JGS.CasperEvent.domain.event.repository.eventRepository;

import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RushEventRepository extends JpaRepository<RushEvent, Long> {
    @Query("SELECT e FROM RushEvent e WHERE DATE(CONVERT_TZ(e.startDateTime, '+00:00', '+09:00')) = :eventDate")
    List<RushEvent> findByEventDate(@Param("eventDate") LocalDate eventDate);

    RushEvent findByRushEventId(Long rushEventId);
}
