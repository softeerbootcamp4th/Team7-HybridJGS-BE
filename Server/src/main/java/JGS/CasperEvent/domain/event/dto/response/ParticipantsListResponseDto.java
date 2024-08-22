package JGS.CasperEvent.domain.event.dto.response;

import java.util.List;

public record ParticipantsListResponseDto<T>(List<T> participantsList, Boolean isLastPage, long totalParticipants) {
}
