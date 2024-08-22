package JGS.CasperEvent.domain.event.controller.eventController;


import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.*;
import JGS.CasperEvent.domain.event.dto.response.rush.RushEventOptionResponseDto;
import JGS.CasperEvent.domain.event.dto.response.rush.RushEventResponseDto;
import JGS.CasperEvent.domain.event.service.eventService.RushEventService;
import JGS.CasperEvent.global.entity.BaseUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "선착순 이벤트 API", description = "선착순 이벤트 (Rush Event) 관련 API 목록입니다.")
@RestController
@RequestMapping("/event/rush")
public class RushEventController {
    private final RushEventService rushEventService;

    public RushEventController(RushEventService rushEventService) {
        this.rushEventService = rushEventService;
    }

    @Operation(summary = "메인화면 선착순 이벤트 리스트 조회", description = "메인화면에 들어갈 선착순 이벤트 6개와 서버 시간 등을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of rush events.")
    @GetMapping
    public ResponseEntity<RushEventListResponseDto> getRushEventListAndServerTime() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(rushEventService.getAllRushEvents());
    }

    @Operation(summary = "응모 여부 체크", description = "해당 유저가 오늘의 이벤트에 응모했는지 여부를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "Successfully checked user participation.")
    @GetMapping("/applied")
    public ResponseEntity<Boolean> checkUserParticipationInRushEvent(HttpServletRequest httpServletRequest) {
        BaseUser user = (BaseUser) httpServletRequest.getAttribute("user");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(rushEventService.isExists(user.getId()));
    }

    @Operation(summary = "선착순 이벤트 응모", description = "해당 유저가 오늘의 이벤트에 응모합니다. optionId 값이 필요합니다. optionId 값이 1이면 왼쪽 선택지, 2이면 오른쪽 선택지에 응모합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully applied for the rush event."),
            @ApiResponse(responseCode = "400", description = "Invalid option ID provided."),
            @ApiResponse(responseCode = "401", description = "Unauthorized to apply for the event.")
    })
    @PostMapping("/options/{optionId}/apply")
    public ResponseEntity<Void> applyRushEvent(HttpServletRequest httpServletRequest, @PathVariable("optionId") int optionId) {
        BaseUser user = (BaseUser) httpServletRequest.getAttribute("user");
        rushEventService.apply(user, optionId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(summary = "실시간 응모 비율 조회", description = "실시간으로 변경되는 응모 비율을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the balance rate.")
    @GetMapping("/balance")
    public ResponseEntity<JGS.CasperEvent.domain.event.dto.response.rush.RushEventResultResponseDto> rushEventRate(HttpServletRequest httpServletRequest) {
        BaseUser user = (BaseUser) httpServletRequest.getAttribute("user");
        JGS.CasperEvent.domain.event.dto.response.rush.RushEventResultResponseDto rushEventRateResponseDto = rushEventService.getRushEventRate(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(rushEventRateResponseDto);
    }

    @Operation(summary = "선착순 이벤트 결과를 조회합니다.", description = "이벤트가 끝나고 나서 최종 결과를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the rush event result.")
    @GetMapping("/result")
    public ResponseEntity<RushEventResultResponseDto> rushEventResult(HttpServletRequest httpServletRequest) {
        BaseUser user = (BaseUser) httpServletRequest.getAttribute("user");
        RushEventResultResponseDto result = rushEventService.getRushEventResult(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @Operation(summary = "오늘의 이벤트를 초기화합니다.", description = "오늘의 이벤트를 전부 초기화하고, 응모 여부도 전부 초기화합니다.")
    @ApiResponse(responseCode = "204", description = "Successfully set today's event in Redis.")
    @GetMapping("/today/test")
    public ResponseEntity<Void> setTodayEvent() {
        rushEventService.setRushEvents();
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(summary = "오늘의 이벤트 옵션을 조회합니다.", description = "이벤트 참여자가 이벤트에 진입했을 때 보여질 옵션 선택지 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved today's rush event options.")
    @GetMapping("/today")
    public ResponseEntity<RushEventResponseDto> getTodayEvent() {
        RushEventResponseDto mainRushEventOptionsResponseDto = rushEventService.getTodayRushEventOptions();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mainRushEventOptionsResponseDto);
    }

    @Operation(summary = "옵션의 결과 Text를 조회합니다.", description = "유저가 응모를 했을 때 보여질 옵션의 상세 Text를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the option result."),
            @ApiResponse(responseCode = "400", description = "Invalid option ID provided.")
    })
    @GetMapping("/options/{optionId}/result")
    public ResponseEntity<RushEventOptionResponseDto> getResultOption(@PathVariable("optionId") int optionId) {
        RushEventOptionResponseDto resultRushEventOptionResponseDto = rushEventService.getRushEventOptionResult(optionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resultRushEventOptionResponseDto);
    }
}
