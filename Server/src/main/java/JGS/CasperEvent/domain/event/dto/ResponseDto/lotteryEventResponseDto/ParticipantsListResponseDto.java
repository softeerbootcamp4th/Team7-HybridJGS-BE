package JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto;

import java.util.concurrent.atomic.AtomicInteger;

public record ParticipantsListResponseDto(List<ParticipantsResponseDto>, Boolean isLastPage, AtomicInteger totalParticipants) {
}
