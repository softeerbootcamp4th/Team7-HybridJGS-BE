package JGS.CasperEvent.domain.event.entity.participants;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class LotteryWinners {
    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ranking;

    private String phoneNumber;
    private int linkClickedCount;
    private int expectation;
    private int appliedCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public  LotteryWinners(LotteryParticipants lotteryParticipants) {
        this.id = lotteryParticipants.getId();
        this.phoneNumber = lotteryParticipants.getBaseUser().getPhoneNumber();
        this.linkClickedCount = lotteryParticipants.getLinkClickedCount();
        this.expectation = lotteryParticipants.getExpectations();
        this.appliedCount = lotteryParticipants.getAppliedCount();
        this.createdAt = lotteryParticipants.getCreatedAt();
        this.updatedAt = lotteryParticipants.getUpdatedAt();
    }

    public LotteryWinners() {

    }
}
