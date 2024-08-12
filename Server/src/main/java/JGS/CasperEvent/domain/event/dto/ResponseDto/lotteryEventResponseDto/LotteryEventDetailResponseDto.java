package JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto;

import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public record LotteryEventDetailResponseDto(
        LocalDate startDate, LocalTime startTime,
        LocalDate endDate, LocalTime endTime,
        int appliedCount, int winnerCount,
        LocalDateTime createdAt, LocalDateTime updatedAt) {

    public static ArrayList<LotteryEventDetailResponseDto> of(List<LotteryEvent> lotteryEvent) {
        ArrayList<LotteryEventDetailResponseDto> lotteryEventDtoList = new ArrayList<>();
        for (LotteryEvent event : lotteryEvent) {
            lotteryEventDtoList.add(new LotteryEventDetailResponseDto(
                    event.getStartDateTime().toLocalDate(),
                    event.getStartDateTime().toLocalTime(),
                    event.getEndDateTime().toLocalDate(),
                    event.getEndDateTime().toLocalTime(),
                    event.getTotalAppliedCount(),
                    event.getWinnerCount(),
                    event.getCreatedAt(),
                    event.getUpdatedAt()));
        }
        return lotteryEventDtoList;
    }
}
