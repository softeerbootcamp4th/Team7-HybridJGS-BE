package JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class LotteryEventRequestDto {

    @NotNull(message = "이벤트 시작 날짜를 지정하세요.")
    private LocalDate startDate;

    @NotNull(message = "이벤트 시작 시간을 지정하세요.")
    private LocalTime startTime;

    @NotNull(message = "이벤트 종료 날짜를 지정하세요.")
    private LocalDate endDate;

    @NotNull(message = "이벤트 시작 시간을 지정하세요.")
    private LocalTime endTime;

    @NotNull(message = "당첨인원 수를 지정하세요.")
    private int winnerCount;
}
