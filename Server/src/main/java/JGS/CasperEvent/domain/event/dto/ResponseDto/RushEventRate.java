package JGS.CasperEvent.domain.event.dto.ResponseDto;

import lombok.Getter;

@Getter
public class RushEventRate {
    long leftOption;
    long rightOption;

    public RushEventRate(long leftOption, long rightOption) {
        this.leftOption = leftOption;
        this.rightOption = rightOption;
    }
}
