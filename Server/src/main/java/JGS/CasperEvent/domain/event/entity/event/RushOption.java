package JGS.CasperEvent.domain.event.entity.event;

import JGS.CasperEvent.global.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
public class RushOption extends BaseEntity{

    @OneToOne
    @JoinColumn(name = "rush_event_id")
    private RushEvent rushEvent;

    @Id
    private int optionId;
    private String mainText;
    private String subText;
    private String resultMainText;
    private String resultSubText;
    private String imageUrl;
}
