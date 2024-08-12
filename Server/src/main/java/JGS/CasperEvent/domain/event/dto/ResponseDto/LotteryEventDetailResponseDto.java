package JGS.CasperEvent.domain.event.dto.ResponseDto;

import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public record LotteryEventDetailResponseDto(LocalDateTime startDateTime, LocalDateTime endDateTime,
                                            AtomicInteger appliedCount, int winnerCount,
                                            LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static ArrayList<LotteryEventDetailResponseDto> of(List<LotteryEvent> lotteryEvent) {
        ArrayList<LotteryEventDetailResponseDto> lotteryEventDtoList = new ArrayList<>();
        for (LotteryEvent event : lotteryEvent) {
            lotteryEventDtoList.add(new LotteryEventDetailResponseDto(
                    event.getStartDateTime(),
                    event.getEndDateTime(),
                    event.getAppliedCount(),
                    event.getWinnerCount(),
                    event.getCreatedAt(),
                    event.getUpdatedAt()));
        }
        return lotteryEventDtoList;
    }
}
