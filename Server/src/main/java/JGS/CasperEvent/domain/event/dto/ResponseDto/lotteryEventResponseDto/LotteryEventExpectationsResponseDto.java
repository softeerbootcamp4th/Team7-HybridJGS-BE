package JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto;

import JGS.CasperEvent.domain.event.dto.response.LotteryEventResponseDto;

import java.util.List;

public record LotteryEventExpectationsResponseDto(List<LotteryEventResponseDto> expectations,
                                                  Boolean isLastPage, long totalExpectations) {
}
