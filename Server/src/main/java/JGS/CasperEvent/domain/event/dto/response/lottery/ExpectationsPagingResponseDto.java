package JGS.CasperEvent.domain.event.dto.response.lottery;

import java.util.List;

public record ExpectationsPagingResponseDto(List<LotteryEventResponseDto> expectations,
                                            Boolean isLastPage, long totalExpectations) {
}
