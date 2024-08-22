package JGS.CasperEvent.domain.event.dto.response.rush;

import JGS.CasperEvent.domain.event.entity.event.RushOption;
import JGS.CasperEvent.global.enums.Position;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RushOptionResponseDto {
    private Long optionId;
    private String mainText;
    private String subText;
    private String resultMainText;
    private String resultSubText;
    private String imageUrl;
    private Position position;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private RushOptionResponseDto(Long optionId, String mainText,
                                  String subText, String resultSubText,
                                  String resultMainText, String imageUrl,
                                  Position position, LocalDateTime createdAt,
                                  LocalDateTime updatedAt) {
        this.optionId = optionId;
        this.mainText = mainText;
        this.subText = subText;
        this.resultSubText = resultSubText;
        this.resultMainText = resultMainText;
        this.imageUrl = imageUrl;
        this.position = position;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static RushOptionResponseDto of(RushOption rushOption) {
        return new RushOptionResponseDto(
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