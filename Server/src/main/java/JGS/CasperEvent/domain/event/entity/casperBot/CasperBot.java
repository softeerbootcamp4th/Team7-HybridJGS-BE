package JGS.CasperEvent.domain.event.entity.casperBot;

import JGS.CasperEvent.domain.event.dto.RequestDto.CasperBotRequestDto;
import JGS.CasperEvent.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;


@Entity
@Getter
@ToString
public class CasperBot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long casperId;

    private String phoneNumber;

    private int eyeShape;
    private int eyePosition;
    private int mouthShape;
    private int color;
    private int sticker;
    private String name;
    private String expectation;

    public CasperBot() {

    }

    public CasperBot(CasperBotRequestDto requestDto, String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.eyeShape = requestDto.getEyeShape();
        this.eyePosition = requestDto.getEyePosition();
        this.mouthShape = requestDto.getMouthShape();
        this.color = requestDto.getColor();
        this.sticker = requestDto.getSticker();
        this.name = requestDto.getName();
        this.expectation = requestDto.getExpectation();
    }
}
