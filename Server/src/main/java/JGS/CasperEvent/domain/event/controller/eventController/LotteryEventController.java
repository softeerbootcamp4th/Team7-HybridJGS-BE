package JGS.CasperEvent.domain.event.controller.eventController;

import JGS.CasperEvent.domain.event.dto.RequestDto.CasperBotRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.CasperBotResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.GetLotteryEvent;
import JGS.CasperEvent.domain.event.dto.ResponseDto.GetLotteryParticipant;
import JGS.CasperEvent.domain.event.service.RedisService.RedisService;
import JGS.CasperEvent.domain.event.service.eventService.LotteryEventService;
import JGS.CasperEvent.global.entity.BaseUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
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
    // 추첨 이벤트 조회 API  -> 가짜 API
    @GetMapping
    public ResponseEntity<GetLotteryEvent> getLotteryEvent(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lotteryEventService.getLotteryEvent());
    }
    // 캐스퍼 봇 생성 API
    @PostMapping("/casperBot")
    public ResponseEntity<CasperBotResponseDto> postCasperBot(
            HttpServletRequest request,
            @RequestBody @Valid CasperBotRequestDto postCasperBot) throws BadRequestException {
        BaseUser user = (BaseUser) request.getAttribute("user");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lotteryEventService.postCasperBot(user, postCasperBot));
    }

    // 응모 여부 조회 API
    @GetMapping("/applied")
    public ResponseEntity<GetLotteryParticipant> GetLotteryParticipant(HttpServletRequest request) throws UserPrincipalNotFoundException {
        BaseUser user = (BaseUser) request.getAttribute("user");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lotteryEventService.getLotteryParticipant(user));
    }

    // 최근 100개 캐스퍼 봇 조회
    @GetMapping("/caspers")
    public ResponseEntity<List<CasperBotResponseDto>> getCasperBots() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(redisService.getRecentData());
    }

    // 캐스퍼 봇 조회 API
    @GetMapping("/{casperId}")
    public ResponseEntity<CasperBotResponseDto> getCasperBot(@PathVariable String casperId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lotteryEventService.getCasperBot(Long.parseLong(casperId)));
    }
}
