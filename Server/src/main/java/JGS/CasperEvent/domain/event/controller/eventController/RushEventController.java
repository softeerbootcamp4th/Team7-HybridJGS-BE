package JGS.CasperEvent.domain.event.controller.eventController;


import JGS.CasperEvent.domain.event.dto.ResponseDto.RushEventListAndServerTimeResponse;
import JGS.CasperEvent.domain.event.dto.ResponseDto.RushEventRate;
import JGS.CasperEvent.domain.event.service.eventService.RushEventService;
import JGS.CasperEvent.global.entity.BaseUser;
import jakarta.servlet.http.HttpServletRequest;
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
    @GetMapping("/{eventId}/applied")
    public ResponseEntity<Boolean> checkUserParticipationInRushEvent(HttpServletRequest httpServletRequest, @PathVariable("eventId") Long eventId) {

        BaseUser user = (BaseUser) httpServletRequest.getAttribute("user");
        return ResponseEntity.ok(rushEventService.isExists(eventId, user.getId()));
    }

    // 밸런스 게임 응모
    @PostMapping("/{eventId}/options/{optionId}/apply")
    public ResponseEntity<Void> applyRushEvent(HttpServletRequest httpServletRequest, @PathVariable("eventId") Long eventId, @PathVariable("optionId") int optionId) {
        BaseUser user = (BaseUser) httpServletRequest.getAttribute("user");
        rushEventService.apply(user, eventId, optionId);

        return ResponseEntity.noContent().build();
    }

    // 밸런스 게임 비율 조회
    @GetMapping("/{eventId}/balance")
    public ResponseEntity<RushEventRate> rushEventRate (@PathVariable("eventId") Long eventId) {
        RushEventRate rushEventRate = rushEventService.getRushEventRate(eventId);
        return ResponseEntity.ok(rushEventRate);
    }
}
