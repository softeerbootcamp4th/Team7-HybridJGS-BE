package JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public record ParticipantsListResponseDto(List<ParticipantsResponseDto> participantsList, Boolean isLastPage, AtomicInteger totalParticipants) {
}
