package JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto;

import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record LotteryEventExpectationResponseDto(String expectation, LocalDate createdDate,
                                                 LocalTime createdTime) {

    public static LotteryEventExpectationResponseDto of(CasperBot casperBot) {

        LocalDateTime createdAt = casperBot.getCreatedAt();
        LocalDate createdDate = createdAt.toLocalDate();
        LocalTime createdTime = createdAt.toLocalTime();

        return new LotteryEventExpectationResponseDto(
                casperBot.getExpectation(),
                createdDate,
                createdTime
        );
    }
}
