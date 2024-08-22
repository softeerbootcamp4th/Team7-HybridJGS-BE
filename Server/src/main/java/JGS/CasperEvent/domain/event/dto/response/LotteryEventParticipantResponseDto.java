package JGS.CasperEvent.domain.event.dto.response;

import java.time.LocalDateTime;

public class LotteryEventParticipantResponseDto {
    private Long id;
    private String phoneNumber;
    private int linkClickedCount;
    private int expectations;
    private int appliedCount;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;


}
