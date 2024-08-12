package JGS.CasperEvent.domain.event.dto.ResponseDto;

import java.time.LocalDate;

public record TotalEventDateResponseDto(LocalDate totalEventStartDate, LocalDate totalEventEndDate) {
}
