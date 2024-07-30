package JGS.CasperEvent.domain.event.entity.casperBot;

import JGS.CasperEvent.domain.event.entity.casperBot.casperEnum.*;
import JGS.CasperEvent.global.entity.BaseEntity;
import com.google.gson.annotations.SerializedName;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
public class CasperBot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int casperId;

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

    public int getCasperId() {
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

    @PostConstruct
    public void validateEnumFields() {
        if (eyeShape == null) {
            throw new IllegalArgumentException("EyeShape cannot be null");
        }
        if (eyePosition == null) {
            throw new IllegalArgumentException("EyePosition cannot be null");
        }
        if (mouthShape == null) {
            throw new IllegalArgumentException("MouthShape cannot be null");
        }
        if (color == null) {
            throw new IllegalArgumentException("Color cannot be null");
        }
        if (sticker == null) {
            throw new IllegalArgumentException("Sticker cannot be null");
        }
    }
}
