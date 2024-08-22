package JGS.CasperEvent.domain.event.dto.response.rush;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RushEventResultResponseDto {
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Integer optionId;
    private Long leftOption;
    private Long rightOption;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Long rank;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Long totalParticipants;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Boolean isWinner;

    private RushEventResultResponseDto(Integer optionId, Long leftOption, Long rightOption) {
        this.optionId = optionId;
        this.leftOption = leftOption;
        this.rightOption = rightOption;
    }

    // RushEventRateResponseDto
    public static RushEventResultResponseDto of(Integer optionId, Long leftOption, Long rightOption) {
        return new RushEventResultResponseDto(optionId, leftOption, rightOption);
    }

    private RushEventResultResponseDto(Integer optionId, Long leftOption,
                                       Long rightOption, Long rank,
                                       Long totalParticipants, Boolean isWinner) {
        this.optionId = optionId;
        this.leftOption = leftOption;
        this.rightOption = rightOption;
        this.rank = rank;
        this.totalParticipants = totalParticipants;
        this.isWinner = isWinner;
    }

    public static RushEventResultResponseDto withDetail(Integer optionId, Long leftOption,
                                                        Long rightOption, Long rank,
                                                        Long totalParticipants, Boolean isWinner){
        return new RushEventResultResponseDto(optionId, leftOption, rightOption, rank, totalParticipants, isWinner);
    }
}
