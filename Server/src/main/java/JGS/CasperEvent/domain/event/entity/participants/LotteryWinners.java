package JGS.CasperEvent.domain.event.entity.participants;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class LotteryWinners {
    @Id
    private long id;
    private String phoneNumber;
    private int linkClickedCount;
    private int expectation;
    private int appliedCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public  LotteryWinners(LotteryParticipants lotteryParticipants) {
        this.id = lotteryParticipants.getId();
        this.phoneNumber = lotteryParticipants.getBaseUser().getId();
        this.linkClickedCount = lotteryParticipants.getLinkClickedCount();
        this.expectation = lotteryParticipants.getExpectations();
        this.appliedCount = lotteryParticipants.getAppliedCount();
        this.createdAt = lotteryParticipants.getCreatedAt();
        this.updatedAt = lotteryParticipants.getUpdatedAt();
    }

    public LotteryWinners() {

    }
}
