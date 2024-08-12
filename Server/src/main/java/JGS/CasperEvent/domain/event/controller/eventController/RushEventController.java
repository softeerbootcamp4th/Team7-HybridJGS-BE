package JGS.CasperEvent.domain.event.controller.eventController;


import JGS.CasperEvent.domain.event.dto.ResponseDto.*;
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
    public ResponseEntity<RushEventListResponseDto> getRushEventListAndServerTime() {
        return ResponseEntity.ok(rushEventService.getAllRushEvents());
    }

    // 밸런스 게임 참여 여부 조회
    @GetMapping("/applied")
    public ResponseEntity<Boolean> checkUserParticipationInRushEvent(HttpServletRequest httpServletRequest) {
        BaseUser user = (BaseUser) httpServletRequest.getAttribute("user");
        return ResponseEntity.ok(rushEventService.isExists(user.getId()));
    }

    // 밸런스 게임 응모
    @PostMapping("/options/{optionId}/apply")
    public ResponseEntity<Void> applyRushEvent(HttpServletRequest httpServletRequest, @PathVariable("optionId") int optionId) {
        BaseUser user = (BaseUser) httpServletRequest.getAttribute("user");
        rushEventService.apply(user, optionId);

        return ResponseEntity.noContent().build();
    }

    // 밸런스 게임 비율 조회
    @GetMapping("/balance")
    public ResponseEntity<RushEventRateResponseDto> rushEventRate(HttpServletRequest httpServletRequest) {
        BaseUser user = (BaseUser) httpServletRequest.getAttribute("user");
        RushEventRateResponseDto rushEventRateResponseDto = rushEventService.getRushEventRate(user);
        return ResponseEntity.ok(rushEventRateResponseDto);
    }

    // 밸런스 게임 결과 조회
    @GetMapping("/result")
    public ResponseEntity<RushEventResultResponseDto> rushEventResult(HttpServletRequest httpServletRequest) {
        BaseUser user = (BaseUser) httpServletRequest.getAttribute("user");
        RushEventResultResponseDto result = rushEventService.getRushEventResult(user);
        return ResponseEntity.ok(result);
    }

    // 레디스에 오늘의 이벤트 등록 테스트 api
    @GetMapping("/today/test")
    public ResponseEntity<Void> setTodayEvent() {
        rushEventService.setTodayEventToRedis();
        return ResponseEntity.noContent().build();
    }

    // 오늘의 이벤트 선택지 조회
    @GetMapping("/today")
    public ResponseEntity<MainRushEventOptionsResponseDto> getTodayEvent() {
        MainRushEventOptionsResponseDto mainRushEventOptionsResponseDto = rushEventService.getTodayRushEventOptions();
        return ResponseEntity.ok(mainRushEventOptionsResponseDto);
    }

    // 옵션 선택 결과 조회
    @GetMapping("/options/{optionId}/result")
    public ResponseEntity<ResultRushEventOptionResponseDto> getResultOption(@PathVariable("optionId") int optionId) {
        ResultRushEventOptionResponseDto resultRushEventOptionResponseDto = rushEventService.getRushEventOptionResult(optionId);
        return ResponseEntity.ok(resultRushEventOptionResponseDto);
    }
}
