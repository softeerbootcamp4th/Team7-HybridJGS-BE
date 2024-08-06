package JGS.CasperEvent.domain.event.controller.eventController;

import JGS.CasperEvent.domain.event.dto.RequestDto.PostCasperBot;
import JGS.CasperEvent.domain.event.dto.ResponseDto.GetCasperBot;
import JGS.CasperEvent.domain.event.dto.ResponseDto.GetLotteryParticipant;
import JGS.CasperEvent.domain.event.service.RedisService.RedisService;
import JGS.CasperEvent.domain.event.service.eventService.LotteryEventService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/event/lottery")
public class LotteryEventController {

    private final LotteryEventService lotteryEventService;
    private final RedisService redisService;

    @Autowired
    public LotteryEventController(LotteryEventService lotteryEventService, RedisService redisService) {
        this.lotteryEventService = lotteryEventService;
        this.redisService = redisService;
    }

    // 캐스퍼 봇 생성 API
    @PostMapping
    public ResponseEntity<GetCasperBot> postCasperBot(
            HttpServletRequest request,
            @RequestBody @Valid PostCasperBot postCasperBot) {
        String userId = request.getAttribute("userId").toString();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lotteryEventService.postCasperBot(userId, postCasperBot));
    }

    // 응모 여부 조회 API
    @GetMapping("/applied")
    public ResponseEntity<GetLotteryParticipant> GetLotteryParticipant(HttpServletRequest request) throws UserPrincipalNotFoundException {
        String userId = request.getAttribute("userId").toString();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lotteryEventService.getLotteryParticipant(userId));
    }

    // 최근 100개 캐스퍼 봇 조회
    @GetMapping("/caspers")
    public ResponseEntity<List<GetCasperBot>> getCasperBots() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(redisService.getRecentData());
    }

    // 캐스퍼 봇 조회 API
    @GetMapping("/{casperId}")
    public ResponseEntity<GetCasperBot> getCasperBot(@PathVariable String casperId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lotteryEventService.getCasperBot(Long.parseLong(casperId)));
    }


}
