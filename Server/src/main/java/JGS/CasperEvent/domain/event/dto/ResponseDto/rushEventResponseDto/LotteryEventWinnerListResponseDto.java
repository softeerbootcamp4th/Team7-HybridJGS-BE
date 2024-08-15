package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import java.util.List;

public record LotteryEventWinnerListResponseDto(List<LotteryEventWinnerResponseDto> participantsList,
                                                Boolean isLastPage, long totalParticipants) {
}