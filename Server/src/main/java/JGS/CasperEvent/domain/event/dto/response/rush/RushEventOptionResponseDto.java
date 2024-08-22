package JGS.CasperEvent.domain.event.dto.response.rush;

import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.ResultRushEventOptionResponseDto;
import JGS.CasperEvent.domain.event.entity.event.RushOption;
import JGS.CasperEvent.global.enums.Position;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RushEventOptionResponseDto {
    private Long optionId;
    private String mainText;
    private String subText;
    private String resultMainText;
    private String resultSubText;
    private String imageUrl;
    private Position position;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private RushEventOptionResponseDto(Long optionId, String mainText,
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

    // RushEventOptionResponseDto
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

    private RushEventOptionResponseDto(String mainText, String subTest) {
        this.mainText = mainText;
        this.subText = subTest;
    }

    // MainRushEventOptionResponseDto
    public static RushEventOptionResponseDto inMain(RushEventOptionResponseDto rushEventOptionResponseDto) {
        return new RushEventOptionResponseDto(
                rushEventOptionResponseDto.getMainText(),
                rushEventOptionResponseDto.getSubText()
        );
    }

    public static RushEventOptionResponseDto inMain(String mainText, String subText){
        return new RushEventOptionResponseDto(mainText, subText);
    }

    private RushEventOptionResponseDto(String mainText, String resultMainText, String resultSubText) {
        this.mainText = mainText;
        this.resultMainText = resultMainText;
        this.resultSubText = resultSubText;
    }

    // ResultRushEventOptionResponseDto
    public static RushEventOptionResponseDto inResult(RushEventOptionResponseDto rushEventOptionResponseDto) {
        return new RushEventOptionResponseDto(
                rushEventOptionResponseDto.getMainText(),
                rushEventOptionResponseDto.getResultMainText(),
                rushEventOptionResponseDto.getResultSubText()
        );
    }
}