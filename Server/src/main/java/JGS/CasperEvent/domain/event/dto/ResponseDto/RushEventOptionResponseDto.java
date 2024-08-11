package JGS.CasperEvent.domain.event.dto.ResponseDto;

import JGS.CasperEvent.domain.event.entity.event.RushOption;
import JGS.CasperEvent.global.enums.Position;

public record RushEventOptionResponseDto(String mainText, String subText, String resultMainText, String resultSubText, String imageUrl, Position position) {
    public static RushEventOptionResponseDto of(RushOption rushOption) {
        return new RushEventOptionResponseDto(
                rushOption.getMainText(),
                rushOption.getSubText(),
                rushOption.getResultMainText(),
                rushOption.getResultSubText(),
                rushOption.getImageUrl(),
                rushOption.getPosition()
        );
    }
}
