package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RushEventResultResponseDto {
    private final Integer optionId;
    private final Long leftOption;
    private final Long rightOption;
    private final Long rank;
    private final Long totalParticipants;
    private final Boolean isWinner;
}
