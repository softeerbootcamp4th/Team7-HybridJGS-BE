package JGS.CasperEvent.domain.event.controller.eventController;


import JGS.CasperEvent.domain.event.dto.ResponseDto.RushEventListAndServerTimeResponse;
import JGS.CasperEvent.domain.event.service.eventService.RushEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event/rush")
public class RushEventController {
    private final RushEventService rushEventService;

    public RushEventController(RushEventService rushEventService) {
        this.rushEventService = rushEventService;
    }

    // 전체 선착순 이벤트 조회
    @GetMapping
    public ResponseEntity<RushEventListAndServerTimeResponse> getRushEventListAndServerTime() {
        return ResponseEntity.ok(rushEventService.getAllRushEvents());
    }

    // 밸런스 게임 참여 여부 조회
//    @GetMapping("/{eventId}/applied")
//    public ResponseEntity<Boolean> checkUserParticipationInRushEvent(@PathVariable Long eventId,
//                                                                     @CookieValue String userData) {
//
//        return ResponseEntity.ok(rushEventService.isExists(eventId, userData));
//    }
}
