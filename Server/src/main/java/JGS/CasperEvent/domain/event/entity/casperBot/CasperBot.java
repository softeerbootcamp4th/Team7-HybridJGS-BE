package JGS.CasperEvent.domain.event.entity.casperBot;

import JGS.CasperEvent.domain.event.entity.casperBot.casperEnum.*;
import JGS.CasperEvent.global.entity.BaseEntity;

public class CasperBot extends BaseEntity {
    private int casperId;
    private String phone_number;
    private EyeShape eyeShape;
    private EyePosition eyePosition;
    private MouthShape mouthShape;
    private Color color;
    private Sticker sticker;
    private String name;
    private String expectation;
}
