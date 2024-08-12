package JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto;

import java.util.List;

public record LotteryEventParticipantsListResponseDto(List<LotteryEventParticipantsResponseDto> participantsList, Boolean isLastPage, long totalParticipants) {
}
