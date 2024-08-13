package JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto;

import JGS.CasperEvent.global.enums.Position;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class RushEventOptionRequestDto {
    private Long rushOptionId;
    private Position position;
    private String mainText;
    private String subText;
    private String resultMainText;
    private String resultSubText;
}
