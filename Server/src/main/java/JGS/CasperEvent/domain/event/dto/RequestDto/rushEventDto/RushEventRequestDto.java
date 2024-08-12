package JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@ToString
@Getter
public class RushEventRequestDto {
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int winnerCount;
    private String prizeDescription;
    @JsonProperty("leftOption")
    private RushEventOptionRequestDto leftOption;
    @JsonProperty("rightOption")
    private RushEventOptionRequestDto rightOption;
}
