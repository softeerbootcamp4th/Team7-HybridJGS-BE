package JGS.CasperEvent.domain.event.entity.casperBot;

import JGS.CasperEvent.domain.event.dto.RequestDto.PostCasperBot;
import JGS.CasperEvent.global.entity.BaseEntity;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.util.UserUtil;
import jakarta.persistence.*;


@Entity
public class CasperBot extends BaseEntity {
    public CasperBot(PostCasperBot postCasperBot, String phoneNumber) {
        this.casperId = UserUtil.generateId();
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

    public void validateEnumFields() throws CustomException {
//        if (eyeShape == null) {
//            throw new CustomException("eyeShape cannot be null", CustomErrorCode.INVALID_CASPERBOT_PARAMETER);
//        }
//        if (eyePosition == null) {
//            throw new CustomException("EyePosition cannot be null", CustomErrorCode.INVALID_CASPERBOT_PARAMETER);
//        }
//        if (mouthShape == null) {
//            throw new CustomException("MouthShape cannot be null", CustomErrorCode.INVALID_CASPERBOT_PARAMETER);
//        }
//        if (color == null) {
//            throw new CustomException("Color cannot be null", CustomErrorCode.INVALID_CASPERBOT_PARAMETER);
//        }
//        if (sticker == null) {
//            throw new CustomException("Sticker cannot be null", CustomErrorCode.INVALID_CASPERBOT_PARAMETER);
//        }
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
