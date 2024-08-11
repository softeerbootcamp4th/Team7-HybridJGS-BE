package JGS.CasperEvent.domain.event.entity.event;

import JGS.CasperEvent.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RushOption extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    @ManyToOne
    @JoinColumn(name = "rush_event_id")
    private RushEvent rushEvent;
    private String mainText;
    private String subText;
    private String resultMainText;
    private String resultSubText;
    private String imageUrl;
}
