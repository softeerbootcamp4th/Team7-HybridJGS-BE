package JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto;

import io.swagger.v3.oas.annotations.media.Schema;
import JGS.CasperEvent.global.enums.Position;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
@Schema(description = "선착순 이벤트 선택지를 생성하기 위한 요청 데이터입니다.")
public class RushEventOptionRequestDto {

    @Schema(description = "선착순 이벤트 선택지 ID", example = "1")
    @NotNull(message = "선착순 이벤트 선택지 ID는 필수 값입니다.")
    private Long rushOptionId;

    @Schema(description = "선택지의 위치", example = "LEFT", implementation = Position.class)
    @NotNull(message = "선택지의 위치는 필수 값입니다.")
    private Position position;

    @Schema(description = "선택지 메인 텍스트", example = "첫 차는 저렴해야 한다")
    @NotNull(message = "선택지 메인 텍스트는 필수 값입니다.")
    private String mainText;

    @Schema(description = "선택지 서브 텍스트", example = "가성비 좋게 저렴한 차로 시작해서 차근히 업그레이드하고 싶어")
    @NotNull(message = "선택지 서브 텍스트는 필수 값입니다.")
    private String subText;

    @Schema(description = "선택지 결과 메인 텍스트", example = "가성비 좋은 도심형 전기차")
    @NotNull(message = "선택지 결과 메인 텍스트는 필수 값입니다.")
    private String resultMainText;

    @Schema(description = "선택지 결과 서브 텍스트", example = "캐스퍼 일렉트릭은 전기차 평균보다 30% 저렴해요 첫 차로 캐스퍼 일렉트릭 어떤가요?")
    @NotNull(message = "선택지 결과 서브 텍스트는 필수 값입니다.")
    private String resultSubText;

    @Schema(description = "선택지 이미지 URL", example = "https://example.com/image.png")
    @NotNull(message = "선택지 이미지 URL은 필수 값입니다.")
    private String imageUrl;
}
