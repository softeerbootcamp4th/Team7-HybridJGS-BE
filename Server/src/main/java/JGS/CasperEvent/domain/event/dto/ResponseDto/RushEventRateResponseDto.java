package JGS.CasperEvent.domain.event.dto.ResponseDto;

import lombok.Getter;

@Getter
public record RushEventRateResponseDto(long leftOption, long rightOption) {
}
