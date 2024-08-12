package JGS.CasperEvent.domain.event.dto.ResponseDto;

import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.entity.event.RushOption;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record RushEventResponseDto(Long rushEventId,
                                   @JsonSerialize(using = LocalDateTimeSerializer.class)
                                   @JsonDeserialize(using = LocalDateTimeDeserializer.class)

                                   LocalDateTime startDateTime,
                                   @JsonSerialize(using = LocalDateTimeSerializer.class)
                                   @JsonDeserialize(using = LocalDateTimeDeserializer.class)

                                   LocalDateTime endDateTime,
                                   int winnerCount, String prizeImageUrl,
                                   String prizeDescription,
                                   Set<RushEventOptionResponseDto> options){

    public static RushEventResponseDto of (RushEvent rushEvent){
        Set<RushEventOptionResponseDto> options = rushEvent.getOptions().stream()
                .map(RushEventOptionResponseDto::of)
                .collect(Collectors.toSet());

        return new RushEventResponseDto(
                rushEvent.getRushEventId(),
                rushEvent.getStartDateTime(),
                rushEvent.getEndDateTime(),
                rushEvent.getWinnerCount(),
                rushEvent.getPrizeImageUrl(),
                rushEvent.getPrizeDescription(),
                options
        );
    }
}
