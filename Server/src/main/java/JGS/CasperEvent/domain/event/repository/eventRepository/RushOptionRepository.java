package JGS.CasperEvent.domain.event.repository.eventRepository;

import JGS.CasperEvent.domain.event.entity.event.RushOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RushOptionRepository extends JpaRepository<RushOption, Long> {
}
