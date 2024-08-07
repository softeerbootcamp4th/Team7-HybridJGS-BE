package JGS.CasperEvent.domain.event.dto.ResponseDto;

import lombok.Getter;

@Getter
public class RushEventRateResponseDto {
    long leftOption;
    long rightOption;

    public RushEventRateResponseDto(long leftOption, long rightOption) {
        this.leftOption = leftOption;
        this.rightOption = rightOption;
    }
}
