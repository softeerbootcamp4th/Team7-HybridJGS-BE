package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import JGS.CasperEvent.domain.event.entity.event.RushOption;
import JGS.CasperEvent.global.enums.Position;

import java.time.LocalDateTime;

public record RushEventOptionResponseDto(long optionId, String mainText,
                                         String subText, String resultMainText,
                                         String resultSubText, String imageUrl,
                                         Position position, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static RushEventOptionResponseDto of(RushOption rushOption) {
        return new RushEventOptionResponseDto(
                rushOption.getOptionId(),
                rushOption.getMainText(),
                rushOption.getSubText(),
                rushOption.getResultMainText(),
                rushOption.getResultSubText(),
                rushOption.getImageUrl(),
                rushOption.getPosition(),
                rushOption.getCreatedAt(),
                rushOption.getUpdatedAt()
        );
    }

}
