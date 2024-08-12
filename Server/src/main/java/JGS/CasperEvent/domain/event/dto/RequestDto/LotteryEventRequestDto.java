package JGS.CasperEvent.domain.event.dto.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LotteryEventRequestDto {

    @NotNull(message = "이벤트 시작 일자를 지정하세요.")
    private LocalDateTime eventStartDateTime;

    @NotNull(message = "이벤트 종료 일자를 지정하세요.")
    private LocalDateTime eventEndDateTime;

    @NotNull(message = "당첨인원 수를 지정하세요.")
    private int winnerCount;
}
