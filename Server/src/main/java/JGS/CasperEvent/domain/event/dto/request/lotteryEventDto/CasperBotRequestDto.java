package JGS.CasperEvent.domain.event.dto.request.lotteryEventDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
@Schema(description = "캐스퍼 봇 생성 요청을 위한 데이터입니다.")
public class CasperBotRequestDto {

    @NotNull(message = "눈 모양 값은 필수 필드입니다.")
    @Min(value = 0, message = "눈 모양 값이 부적절합니다.")
    @Max(value = 7, message = "눈 모양 값이 부적절합니다.")
    @Schema(description = "눈 모양 값", example = "1")
    private Integer eyeShape;

    @NotNull(message = "눈 위치 값은 필수 필드입니다.")
    @Min(value = 0, message = "눈 위치 값이 부적절합니다.")
    @Max(value = 2, message = "눈 위치 값이 부적절합니다.")
    @Schema(description = "눈 위치 값", example = "1")
    private Integer eyePosition;

    @NotNull(message = "입 모양 값은 필수 필드입니다.")
    @Min(value = 0, message = "입 모양 값이 부적절합니다.")
    @Max(value = 4, message = "입 모양 값이 부적절합니다.")
    @Schema(description = "입 모양 값", example = "2")
    private Integer mouthShape;

    @NotNull(message = "색깔 값은 필수 필드입니다.")
    @Min(value = 0, message = "색깔 값이 부적절합니다.")
    @Max(value = 17, message = "색깔 값이 부적절합니다.")
    @Schema(description = "색깔 값", example = "8")
    private Integer color;

    @NotNull(message = "스티커 값은 필수 필드입니다.")
    @Min(value = 0, message = "스티커 값이 부적절합니다.")
    @Max(value = 4, message = "스티커 값이 부적절합니다.")
    @Schema(description = "스티커 값", example = "1")
    private Integer sticker;

    @NotNull(message = "이름은 필수 필드입니다.")
    @Schema(description = "캐스퍼 봇의 이름", example = "MyCasperBot")
    private String name;

    @Schema(description = "기대평", example = "캐스퍼 정말 기대되요!")
    private String expectation;

    @Schema(description = "추천인 ID", example = "referralId")
    private String referralId = "";
}
