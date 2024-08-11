package JGS.CasperEvent.domain.event.dto.ResponseDto;

public record RushEventRateResponseDto(long leftOption, long rightOption) {
    public long getLeftOption() {
        return leftOption;
    }

    public long getRightOption() {
        return rightOption;
    }
}
