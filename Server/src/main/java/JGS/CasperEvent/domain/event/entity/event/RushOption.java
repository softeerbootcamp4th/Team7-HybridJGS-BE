package JGS.CasperEvent.domain.event.entity.event;

import JGS.CasperEvent.global.entity.BaseEntity;
import jakarta.persistence.Column;

public class RushOption extends BaseEntity{

    @Column(name = "rush_event_id")
    private Long rushEventId;
    private int optionId;
    private String mainText;
    private String subText;
    private String resultMainText;
    private String resultSubText;
    private String imageUrl;
}
