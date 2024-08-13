package JGS.CasperEvent.domain.event.repository;

import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CasperBotRepository extends JpaRepository<CasperBot, Long> {
    List<CasperBot> findByPhoneNumber(String phoneNumber);
}
