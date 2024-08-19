package JGS.CasperEvent.domain.event.entity.event;

import JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto.RushEventRequestDto;
import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.enums.Position;
import JGS.CasperEvent.global.error.exception.CustomException;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@ToString
@EqualsAndHashCode
public class RushEvent extends BaseEvent {
    private String prizeImageUrl;
    private String prizeDescription;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rushEventId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "rushEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final Set<RushOption> options = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "rushEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RushParticipants> rushParticipants;

    public RushEvent() {
    }

    public RushEvent(String prizeImageUrl, String prizeDescription) {
        super();
        this.prizeImageUrl = prizeImageUrl;
        this.prizeDescription = prizeDescription;
    }


    public RushEvent(LocalDateTime startDateTime, LocalDateTime endDateTime,
                     int winnerCount, String prizeImageUrl, String prizeDescription) {
        super(startDateTime, endDateTime, winnerCount);
        this.prizeImageUrl = prizeImageUrl;
        this.prizeDescription = prizeDescription;
    }

    public void addOption(RushOption leftOption, RushOption rightOption) {
        this.options.add(leftOption);
        this.options.add(rightOption);
    }

    public RushEvent updateRushEvent(RushEventRequestDto requestDto) {
        this.startDateTime = LocalDateTime.of(requestDto.getEventDate(), requestDto.getStartTime());
        this.endDateTime = LocalDateTime.of(requestDto.getEventDate(), requestDto.getEndTime());
        this.winnerCount = requestDto.getWinnerCount();
        this.prizeDescription = requestDto.getPrizeDescription();
        this.prizeImageUrl = requestDto.getPrizeImageUrl();
        return this;
    }

    public RushOption getLeftOption() {
        return options.stream()
                .filter(option -> option.getPosition() == Position.LEFT)
                .findFirst().orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_RUSH_EVENT_OPTION));
    }

    public RushOption getRightOption() {
        return options.stream()
                .filter(option -> option.getPosition() == Position.RIGHT)
                .findFirst().orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_RUSH_EVENT_OPTION));
    }
}
