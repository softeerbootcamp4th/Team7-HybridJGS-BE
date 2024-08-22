package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import JGS.CasperEvent.domain.event.dto.response.lottery.LotteryEventParticipantResponseDto;

import java.util.List;

public record LotteryEventWinnerListResponseDto(List<LotteryEventParticipantResponseDto> participantsList,
                                                Boolean isLastPage, long totalParticipants) {
}