package JGS.CasperEvent.domain.event.dto;

import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import JGS.CasperEvent.domain.event.entity.casperBot.casperEnum.*;

public record GetCasperBot(EyeShape eyeShape, EyePosition eyePosition,
                           MouthShape mouthShape, Color color,
                           Sticker sticker, String name, String expectation) {

    public static GetCasperBot of(CasperBot casperBot){
        return new GetCasperBot(
                casperBot.getEyeShape(),
                casperBot.getEyePosition(),
                casperBot.getMouthShape(),
                casperBot.getColor(),
                casperBot.getSticker(),
                casperBot.getName(),
                casperBot.getExpectation()
        );
    }
}
