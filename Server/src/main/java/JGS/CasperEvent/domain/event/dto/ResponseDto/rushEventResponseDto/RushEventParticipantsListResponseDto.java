package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import JGS.CasperEvent.domain.event.dto.response.rush.RushEventParticipantResponseDto;

import java.util.List;
public record RushEventParticipantsListResponseDto(List<RushEventParticipantResponseDto> participantsList, Boolean isLastPage, long totalParticipants) {
}
