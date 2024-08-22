package JGS.CasperEvent.domain.event.dto.response.rush;

import java.time.LocalDateTime;

public class RushEventResponseDto {
    private Long rushEventId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int winnerCount;
    private String prizeImageUrl;
    private Set<> options;
}
