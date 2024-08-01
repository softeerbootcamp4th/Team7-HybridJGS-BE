package JGS.CasperEvent.domain.event.entity.casperBot;

import JGS.CasperEvent.domain.event.entity.casperBot.casperEnum.*;
import JGS.CasperEvent.global.entity.BaseEntity;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.error.exception.ErrorCode;
import JGS.CasperEvent.global.util.UserUtil;
import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;


@Entity
public class CasperBot extends BaseEntity {

    public CasperBot(CasperBot casperBot, String phoneNumber){
        this.casperId = UserUtil.generateId();
        this.phoneNumber = phoneNumber;
        this.eyeShape = casperBot.getEyeShape();
        this.eyePosition = casperBot.getEyePosition();
        this.mouthShape = casperBot.getMouthShape();
        this.color = casperBot.getColor();
        this.sticker = casperBot.getSticker();
        this.name = casperBot.getName();
        this.expectation = casperBot.getExpectation();
    }

    @Id
    private Long casperId;

    private String phoneNumber;

    @SerializedName("eyeShape")
    @Enumerated(EnumType.STRING)
    private EyeShape eyeShape;

    @SerializedName("eyePosition")
    @Enumerated(EnumType.STRING)
    private EyePosition eyePosition;

    @SerializedName("mouthShape")
    @Enumerated(EnumType.STRING)
    private MouthShape mouthShape;

    @SerializedName("color")
    @Enumerated(EnumType.STRING)
    private Color color;

    @SerializedName("sticker")
    @Enumerated(EnumType.STRING)
    private Sticker sticker;
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

    public EyeShape getEyeShape() {
        return eyeShape;
    }

    public EyePosition getEyePosition() {
        return eyePosition;
    }

    public MouthShape getMouthShape() {
        return mouthShape;
    }

    public Color getColor() {
        return color;
    }

    public Sticker getSticker() {
        return sticker;
    }

    public String getName() {
        return name;
    }

    public String getExpectation() {
        return expectation;
    }

    public void validateEnumFields() throws CustomException {
        if (eyeShape == null) {
            throw new CustomException("eyeShape cannot be null", ErrorCode.INVALID_REQUEST_ERROR);
        }
        if (eyePosition == null) {
            throw new CustomException("EyePosition cannot be null", ErrorCode.INVALID_REQUEST_ERROR);
        }
        if (mouthShape == null) {
            throw new CustomException("MouthShape cannot be null", ErrorCode.INVALID_REQUEST_ERROR);
        }
        if (color == null) {
            throw new CustomException("Color cannot be null", ErrorCode.INVALID_REQUEST_ERROR);
        }
        if (sticker == null) {
            throw new CustomException("Sticker cannot be null", ErrorCode.INVALID_REQUEST_ERROR);
        }
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
