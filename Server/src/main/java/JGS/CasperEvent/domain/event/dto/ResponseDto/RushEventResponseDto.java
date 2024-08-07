package JGS.CasperEvent.domain.event.dto.ResponseDto;

import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.entity.event.RushOption;

import java.time.LocalDate;

public record RushEventResponseDto(Long rushEventId, LocalDate startDate, LocalDate endDate,
                                   int winnerCount, String prizeImageUrl, String prizeDescription,
                                   RushOption leftOption, RushOption rightOption){

    public static RushEventResponseDto of (RushEvent rushEvent){
        return new RushEventResponseDto(
                rushEvent.getRushEventId(),
                rushEvent.getStartDate(),
                rushEvent.getEndDate(),
                rushEvent.getWinnerCount(),
                rushEvent.getPrizeImageUrl(),
                rushEvent.getPrizeDescription(),
                rushEvent.getLeftOption(),
                rushEvent.getRightOption()
        );
    }

}
