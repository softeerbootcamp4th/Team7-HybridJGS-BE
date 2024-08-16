package JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto;

import java.util.List;

public record LotteryEventExpectationsResponseDto(List<LotteryEventExpectationResponseDto> expectations,
                                                  Boolean isLastPage, long totalExpectations) {
}
