package JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto;

import io.swagger.v3.oas.annotations.media.Schema;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.enums.Position;
import JGS.CasperEvent.global.error.exception.CustomException;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@ToString
@Getter
@Builder
@EqualsAndHashCode
@Schema(description = "선착순 이벤트 요청을 위한 데이터입니다.")
public class RushEventRequestDto {

    @Schema(description = "선착순 이벤트 ID", example = "1")
    @NotNull(message = "선착순 이벤트 아이디는 필수 항목입니다.")
    private Long rushEventId;

    @Schema(description = "이벤트 날짜", example = "2024-08-20")
    @NotNull(message = "이벤트 시작 날짜는 필수 항목입니다.")
    private LocalDate eventDate;

    @Schema(description = "이벤트 시작 시간", example = "14:00:00")
    @NotNull(message = "이벤트 시작 시간은 필수 항목입니다.")
    private LocalTime startTime;

    @Schema(description = "이벤트 종료 시간", example = "16:00:00")
    @NotNull(message = "이벤트 종료 시간은 필수 항목입니다.")
    private LocalTime endTime;

    @Schema(description = "당첨자 수", example = "3")
    @NotNull(message = "당첨자 수는 필수 항목입니다.")
    private int winnerCount;

    @Schema(description = "상품 사진 URL", example = "https://example.com/image.png")
    @NotNull(message = "상품 사진은 필수 항목입니다.")
    private String prizeImageUrl;

    @Schema(description = "상품 상세 설명", example = "올리브영 1만원권")
    @NotNull(message = "상품 상세 정보는 필수 항목입니다.")
    private String prizeDescription;

    @Schema(description = "선택지 목록")
    private Set<RushEventOptionRequestDto> options;

    public RushEventOptionRequestDto getLeftOptionRequestDto() {
        return options.stream()
                .filter(option -> option.getPosition() == Position.LEFT)
                .findFirst().orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_RUSH_EVENT_OPTION));
    }

    public RushEventOptionRequestDto getRightOptionRequestDto() {
        return options.stream()
                .filter(option -> option.getPosition() == Position.RIGHT)
                .findFirst().orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_RUSH_EVENT_OPTION));
    }
}
