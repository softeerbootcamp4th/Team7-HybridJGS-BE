package JGS.CasperEvent.domain.event.controller.eventController;

import JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto.CasperBotRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.CasperBotResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryParticipantResponseDto;
import JGS.CasperEvent.domain.event.dto.response.LotteryEventResponseDto;
import JGS.CasperEvent.domain.event.service.redisService.RedisService;
import JGS.CasperEvent.domain.event.service.eventService.LotteryEventService;
import JGS.CasperEvent.global.entity.BaseUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Tag(name = "추첨 이벤트 API", description = "추첨 이벤트 (Lottery Event) 관련 API 목록입니다.")
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

    @Operation(summary = "추첨 이벤트 조회", description = "현재 진행 중인 추첨 이벤트의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lottery event retrieval successful"),
            @ApiResponse(responseCode = "404", description = "No lottery event found in the database"),
            @ApiResponse(responseCode = "409", description = "More than one lottery event exists in the database")
    })
    @GetMapping
    public ResponseEntity<LotteryEventResponseDto> getLotteryEvent() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lotteryEventService.getLotteryEvent());
    }

    @Operation(summary = "캐스퍼 봇 생성", description = "새로운 캐스퍼 봇을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Casper bot creation successful"),
            @ApiResponse(responseCode = "404", description = "No lottery event found in the database"),
            @ApiResponse(responseCode = "409", description = "More than one lottery event exists in the database")
    })
    @PostMapping("/casperBot")
    public ResponseEntity<CasperBotResponseDto> postCasperBot(
            HttpServletRequest request,
            @RequestBody @Valid CasperBotRequestDto postCasperBot) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        BaseUser user = (BaseUser) request.getAttribute("user");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lotteryEventService.postCasperBot(user, postCasperBot));
    }

    @Operation(summary = "응모 여부 조회", description = "현재 사용자의 응모 여부를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application status retrieval successful"),
            @ApiResponse(responseCode = "404", description = "User has not applied")
    })
    @GetMapping("/applied")
    public ResponseEntity<LotteryParticipantResponseDto> getLotteryParticipant(HttpServletRequest request) {
        BaseUser user = (BaseUser) request.getAttribute("user");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lotteryEventService.getLotteryParticipant(user));
    }

    @Operation(summary = "최근 100개 캐스퍼 봇 조회", description = "최근에 생성된 100개의 캐스퍼 봇을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recent Casper bots retrieval successful")
    })
    @GetMapping("/caspers")
    public ResponseEntity<List<CasperBotResponseDto>> getCasperBots() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(redisService.getRecentData());
    }
}
