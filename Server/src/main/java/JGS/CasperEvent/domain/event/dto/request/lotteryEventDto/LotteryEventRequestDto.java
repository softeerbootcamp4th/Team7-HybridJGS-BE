package JGS.CasperEvent.domain.event.dto.request.lotteryEventDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@EqualsAndHashCode
@Schema(description = "추첨 이벤트를 생성하기 위한 요청 데이터입니다.")
public class LotteryEventRequestDto {

    @NotNull(message = "이벤트 시작 날짜를 지정하세요.")
    @Schema(description = "이벤트의 시작 날짜", example = "2024-09-01")
    private LocalDate startDate;

    @NotNull(message = "이벤트 시작 시간을 지정하세요.")
    @Schema(description = "이벤트의 시작 시간", example = "14:00:00")
    private LocalTime startTime;

    @NotNull(message = "이벤트 종료 날짜를 지정하세요.")
    @Schema(description = "이벤트의 종료 날짜", example = "2024-09-30")
    private LocalDate endDate;

    @NotNull(message = "이벤트 종료 시간을 지정하세요.")
    @Schema(description = "이벤트의 종료 시간", example = "18:00:00")
    private LocalTime endTime;

    @NotNull(message = "당첨인원 수를 지정하세요.")
    @Schema(description = "당첨 인원 수", example = "10")
    private int winnerCount;
}
