package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import java.util.List;
public record RushEventParticipantsListResponseDto(List<RushEventParticipantResponseDto> participantsList, Boolean isLastPage, long totalParticipants) {
}
