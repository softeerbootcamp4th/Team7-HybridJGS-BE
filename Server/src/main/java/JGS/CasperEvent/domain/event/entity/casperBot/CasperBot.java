package JGS.CasperEvent.domain.event.entity.casperBot;

import JGS.CasperEvent.domain.event.dto.RequestDto.CasperBotRequestDto;
import JGS.CasperEvent.global.entity.BaseEntity;
import jakarta.persistence.*;


@Entity
public class CasperBot extends BaseEntity {
    public CasperBot(CasperBotRequestDto postCasperBot, String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.eyeShape = postCasperBot.getEyeShape();
        this.eyePosition = postCasperBot.getEyePosition();
        this.mouthShape = postCasperBot.getMouthShape();
        this.color = postCasperBot.getColor();
        this.sticker = postCasperBot.getSticker();
        this.name = postCasperBot.getName();
        this.expectation = postCasperBot.getExpectation();
    }

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

    public Long getCasperId() {
        return casperId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getEyeShape() {
        return eyeShape;
    }

    public int getEyePosition() {
        return eyePosition;
    }

    public int getMouthShape() {
        return mouthShape;
    }

    public int getColor() {
        return color;
    }

    public int getSticker() {
        return sticker;
    }

    public String getName() {
        return name;
    }

    public String getExpectation() {
        return expectation;
    }

    @Override
    public String toString() {
        return "CasperBot{" +
                "casperId=" + casperId +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", eyeShape=" + eyeShape +
                ", eyePosition=" + eyePosition +
                ", mouthShape=" + mouthShape +
                ", color=" + color +
                ", sticker=" + sticker +
                ", name='" + name + '\'' +
                ", expectation='" + expectation + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", updatedAt='" + getUpdatedAt() + '\'' +
                '}';
    }
}
