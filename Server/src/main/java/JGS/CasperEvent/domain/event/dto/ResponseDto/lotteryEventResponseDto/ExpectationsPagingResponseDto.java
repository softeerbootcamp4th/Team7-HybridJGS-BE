package JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto;

import JGS.CasperEvent.domain.event.dto.response.lottery.LotteryEventResponseDto;

import java.util.List;

public record ExpectationsPagingResponseDto(List<LotteryEventResponseDto> expectations,
                                            Boolean isLastPage, long totalExpectations) {
}
