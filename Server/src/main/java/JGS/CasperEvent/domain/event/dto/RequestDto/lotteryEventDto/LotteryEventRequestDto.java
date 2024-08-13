package JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class LotteryEventRequestDto {

    @NotNull(message = "이벤트 시작 날짜를 지정하세요.")
    private LocalDate eventStartDate;

    @NotNull(message = "이벤트 시작 시간을 지정하세요.")
    private LocalTime eventStartTime;

    @NotNull(message = "이벤트 종료 날짜를 지정하세요.")
    private LocalDate eventEndDate;

    @NotNull(message = "이벤트 시작 시간을 지정하세요.")
    private LocalTime eventEndTime;

    @NotNull(message = "당첨인원 수를 지정하세요.")
    private int winnerCount;
}
