package JGS.CasperEvent.domain.url.repository;

import JGS.CasperEvent.domain.url.entity.OriginalUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<OriginalUrl, Long> {
}
