package JGS.CasperEvent.domain.event.repository.eventRepository;

import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RushEventRepository extends JpaRepository<RushEvent, Long> {
}
