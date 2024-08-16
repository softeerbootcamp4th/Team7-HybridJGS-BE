package JGS.CasperEvent.domain.event.controller.eventController;


import JGS.CasperEvent.domain.event.dto.ResponseDto.TotalEventDateResponseDto;
import JGS.CasperEvent.domain.event.service.eventService.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event")

public class EventController {
    private final EventService eventService;

    @GetMapping("/total")
    public ResponseEntity<TotalEventDateResponseDto> getTotalEventDate() {
        TotalEventDateResponseDto totalEventDateResponseDto = eventService.getTotalEventDate();
        return ResponseEntity.ok(totalEventDateResponseDto);
    }
}
