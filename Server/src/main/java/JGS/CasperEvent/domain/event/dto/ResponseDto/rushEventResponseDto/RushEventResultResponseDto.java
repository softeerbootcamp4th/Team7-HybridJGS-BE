package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RushEventResultResponseDto {
    private final long leftOption;
    private final long rightOption;
    private final long rank;
    private final long totalParticipants;
    private final boolean isWinner;

    public RushEventResultResponseDto(RushEventRateResponseDto rushEventRateResponseDto, long rank, long totalParticipants, boolean isWinner) {
        this.leftOption = rushEventRateResponseDto.leftOption();
        this.rightOption = rushEventRateResponseDto.rightOption();
        this.rank = rank;
        this.totalParticipants = totalParticipants;
        this.isWinner = isWinner;
    }
}
