package JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto;

import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.enums.Position;
import JGS.CasperEvent.global.error.exception.CustomException;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@ToString
@Getter
public class RushEventRequestDto {
    private Long rushEventId;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int winnerCount;
    private String prizeDescription;
    private Set<RushEventOptionRequestDto> options;

    public RushEventOptionRequestDto getLeftOption() {
        return options.stream()
                .filter(option -> option.getPosition() == Position.LEFT)
                .findFirst().orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_RUSH_EVENT_OPTION));
    }

    public RushEventOptionRequestDto getRightOption() {
        return options.stream()
                .filter(option -> option.getPosition() == Position.RIGHT)
                .findFirst().orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_RUSH_EVENT_OPTION));
    }
}
