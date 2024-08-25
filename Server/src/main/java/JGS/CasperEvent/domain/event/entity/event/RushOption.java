package JGS.CasperEvent.domain.event.entity.event;

import JGS.CasperEvent.domain.event.dto.request.rushEventDto.RushEventOptionRequestDto;
import JGS.CasperEvent.global.entity.BaseEntity;
import JGS.CasperEvent.global.enums.Position;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"rushEvent"}, callSuper = false)
@ToString(exclude = {"rushEvent"})
public class RushOption extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    @ManyToOne
    @JoinColumn(name = "rush_event_id")
    @JsonBackReference
    private RushEvent rushEvent;
    private String mainText;
    private String subText;
    private String resultMainText;
    private String resultSubText;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Position position;

    public RushOption(RushEvent rushEvent, String mainText, String subText, String resultMainText, String resultSubText, String imageUrl, Position position) {
        this.rushEvent = rushEvent;
        this.mainText = mainText;
        this.subText = subText;
        this.resultMainText = resultMainText;
        this.resultSubText = resultSubText;
        this.imageUrl = imageUrl;
        this.position = position;
    }

    public RushOption updateRushOption(RushEventOptionRequestDto requestDto) {
        this.mainText = requestDto.getMainText();
        this.subText = requestDto.getSubText();
        this.resultMainText = requestDto.getResultMainText();
        this.resultSubText = requestDto.getResultSubText();
        this.imageUrl = requestDto.getImageUrl();
        return this;
    }
}
