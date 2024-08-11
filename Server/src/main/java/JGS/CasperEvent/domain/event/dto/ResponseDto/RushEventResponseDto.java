package JGS.CasperEvent.domain.event.dto.ResponseDto;

import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.entity.event.RushOption;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record RushEventResponseDto(Long rushEventId, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                   int winnerCount, String prizeImageUrl, String prizeDescription,
                                   Set<RushOption> options){

    public static RushEventResponseDto of (RushEvent rushEvent){
        return new RushEventResponseDto(
                rushEvent.getRushEventId(),
                rushEvent.getStartDateTime(),
                rushEvent.getEndDateTime(),
                rushEvent.getWinnerCount(),
                rushEvent.getPrizeImageUrl(),
                rushEvent.getPrizeDescription(),
                rushEvent.getOptions()
        );
    }

}
