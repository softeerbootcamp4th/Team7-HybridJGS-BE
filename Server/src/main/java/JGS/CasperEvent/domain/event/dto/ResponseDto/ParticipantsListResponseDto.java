package JGS.CasperEvent.domain.event.dto.ResponseDto;

import java.util.List;

public record ParticipantsListResponseDto<T>(List<T> participantsList, Boolean isLastPage, long totalParticipants) {
}
