package JGS.CasperEvent.domain.event.entity.casperBot;

import JGS.CasperEvent.domain.event.entity.casperBot.casperEnum.*;
import JGS.CasperEvent.global.entity.BaseEntity;
import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
public class CasperBot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int casperId;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private EyeShape eyeShape;

    @Enumerated(EnumType.STRING)
    private EyePosition eyePosition;

    @Enumerated(EnumType.STRING)
    private MouthShape mouthShape;

    @Enumerated(EnumType.STRING)
    private Color color;

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
}
