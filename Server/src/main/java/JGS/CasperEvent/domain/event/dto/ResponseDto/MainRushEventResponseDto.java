package JGS.CasperEvent.domain.event.dto.ResponseDto;

import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MainRushEventResponseDto {
    private Long rushEventId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public MainRushEventResponseDto(Long rushEventId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.rushEventId = rushEventId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public static MainRushEventResponseDto of (RushEvent rushEvent) {
        return new MainRushEventResponseDto(
                rushEvent.getRushEventId(),
                rushEvent.getStartDateTime(),
                rushEvent.getEndDateTime()
        );
    }
}
