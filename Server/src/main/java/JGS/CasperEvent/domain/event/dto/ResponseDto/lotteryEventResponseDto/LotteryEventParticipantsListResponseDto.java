package JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto;

import JGS.CasperEvent.domain.event.dto.response.LotteryEventParticipantResponseDto;

import java.util.List;

public record LotteryEventParticipantsListResponseDto(List<LotteryEventParticipantResponseDto> participantsList, Boolean isLastPage, long totalParticipants) {
}
