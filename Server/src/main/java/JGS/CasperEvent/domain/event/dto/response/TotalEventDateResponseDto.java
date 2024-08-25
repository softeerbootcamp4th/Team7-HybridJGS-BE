package JGS.CasperEvent.domain.event.dto.response;

import java.time.LocalDate;

public record TotalEventDateResponseDto(LocalDate totalEventStartDate, LocalDate totalEventEndDate) {
}
