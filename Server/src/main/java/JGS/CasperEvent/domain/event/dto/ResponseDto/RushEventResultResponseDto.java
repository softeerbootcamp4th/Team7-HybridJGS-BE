package JGS.CasperEvent.domain.event.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public record RushEventResultResponseDto(long leftOption, long rightOption, long rank, long totalParticipants,
                                         long winnerCount) {
}
