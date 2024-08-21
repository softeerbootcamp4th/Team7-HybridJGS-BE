package JGS.CasperEvent.domain.event.repository;

import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CasperBotRepository extends JpaRepository<CasperBot, Long> {
    @Query("SELECT c FROM CasperBot c WHERE c.phoneNumber = :phoneNumber AND c.isDeleted = false AND c.expectation <> ''")
    Page<CasperBot> findByPhoneNumberAndActiveExpectations(@Param("phoneNumber") String phoneNumber, Pageable pageable);
}
