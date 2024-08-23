package JGS.CasperEvent.domain.event.dto.response.rush;

import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.global.enums.EventStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RushEventResponseDto {
    private Long rushEventId;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int winnerCount;
    private String prizeImageUrl;
    private String prizeDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private EventStatus status;

    private Set<RushEventOptionResponseDto> options;
    private RushEventOptionResponseDto leftOption;
    private RushEventOptionResponseDto rightOption;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private RushEventResponseDto(Long rushEventId, LocalDateTime startDateTime,
                                 LocalDateTime endDateTime, int winnerCount,
                                 String prizeImageUrl, String prizeDescription,
                                 Set<RushEventOptionResponseDto> options) {
        this.rushEventId = rushEventId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.winnerCount = winnerCount;
        this.prizeImageUrl = prizeImageUrl;
        this.prizeDescription = prizeDescription;
        this.options = options;
    }


    public static RushEventResponseDto of(Long rushEventId, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                          int winnerCount, String prizeImageUrl,
                                          String prizeDescription,
                                          Set<RushEventOptionResponseDto> options) {
        return new RushEventResponseDto(
                rushEventId, startDateTime, endDateTime, winnerCount, prizeImageUrl,
                prizeDescription, options);
    }

    // RushEventResponseDto
    public static RushEventResponseDto of(RushEvent rushEvent) {
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

    private RushEventResponseDto(Long rushEventId, LocalDate eventDate,
                                 LocalTime startTime, LocalTime endTime,
                                 int winnerCount, String prizeImageUrl,
                                 String prizeDescription, LocalDateTime createdAt,
                                 LocalDateTime updatedAt, EventStatus status,
                                 Set<RushEventOptionResponseDto> options) {
        this.rushEventId = rushEventId;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.winnerCount = winnerCount;
        this.prizeImageUrl = prizeImageUrl;
        this.prizeDescription = prizeDescription;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.options = options;
    }

    // AdminRushEventResponseDto
    public static RushEventResponseDto withDetail(RushEvent rushEvent) {
        Set<RushEventOptionResponseDto> options = rushEvent.getOptions().stream()
                .map(RushEventOptionResponseDto::of)
                .collect(Collectors.toSet());

        LocalDateTime now = LocalDateTime.now();
        EventStatus status;
        if (now.isBefore(rushEvent.getStartDateTime())) status = EventStatus.BEFORE;
        else if (now.isAfter(rushEvent.getEndDateTime())) status = EventStatus.AFTER;
        else status = EventStatus.DURING;

        return new RushEventResponseDto(
                rushEvent.getRushEventId(),
                rushEvent.getStartDateTime().toLocalDate(),
                rushEvent.getStartDateTime().toLocalTime(),
                rushEvent.getEndDateTime().toLocalTime(),
                rushEvent.getWinnerCount(),
                rushEvent.getPrizeImageUrl(),
                rushEvent.getPrizeDescription(),
                rushEvent.getCreatedAt(),
                rushEvent.getUpdatedAt(),
                status,
                options
        );
    }

    private RushEventResponseDto(Long rushEventId, LocalDateTime startDateTime,
                                 LocalDateTime endDateTime) {
        this.rushEventId = rushEventId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    // MainRushEventResponseDto
    public static RushEventResponseDto withMain(Long rushEventId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return new RushEventResponseDto(
                rushEventId, startDateTime, endDateTime
        );
    }

    public static RushEventResponseDto withMain(RushEvent rushEvent) {
        return new RushEventResponseDto(
                rushEvent.getRushEventId(),
                rushEvent.getStartDateTime(),
                rushEvent.getEndDateTime()
        );
    }

    private RushEventResponseDto(Set<RushEventOptionResponseDto> options) {
        this.options = options;
    }

    // AdminRushEventOptionResponseDto
    public static RushEventResponseDto withOptions(RushEvent rushEvent) {
        Set<RushEventOptionResponseDto> optionResponseDtoList = new HashSet<>();
        optionResponseDtoList.add(RushEventOptionResponseDto.of(rushEvent.getLeftOption()));
        optionResponseDtoList.add(RushEventOptionResponseDto.of(rushEvent.getRightOption()));
        return new RushEventResponseDto(optionResponseDtoList);
    }

    private RushEventResponseDto(RushEventOptionResponseDto leftOption,
                                 RushEventOptionResponseDto rightOption) {
        this.leftOption = leftOption;
        this.rightOption = rightOption;
    }

    // MainRushEventOptionsResponseDto
    public static RushEventResponseDto withMainOption(RushEventOptionResponseDto leftOption,
                                                      RushEventOptionResponseDto rightOption) {
        return new RushEventResponseDto(leftOption, rightOption);
    }
}
