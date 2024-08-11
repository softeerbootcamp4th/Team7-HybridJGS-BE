package JGS.CasperEvent.domain.event.entity.event;

import JGS.CasperEvent.global.entity.BaseEntity;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public class BaseEvent extends BaseEntity {
    protected LocalDateTime eventStartDate;
    protected LocalDateTime eventEndDate;
    protected int winnerCount;
}
