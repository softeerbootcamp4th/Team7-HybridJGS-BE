package JGS.CasperEvent.domain.event.dto.ResponseDto;

import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "casperBot")
public record GetCasperBot(@Id Long casperId, int eyeShape, int eyePosition,
                           int mouthShape, int color,
                           int sticker, String name, String expectation) {

    public static GetCasperBot of(CasperBot casperBot) {
        return new GetCasperBot(
                casperBot.getCasperId(),
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
