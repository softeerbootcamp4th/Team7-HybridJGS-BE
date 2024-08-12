package JGS.CasperEvent.domain.event.controller.adminController;

import JGS.CasperEvent.domain.event.dto.RequestDto.AdminRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto.LotteryEventRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventDetailResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventParticipantsListResponseDto;
import JGS.CasperEvent.domain.event.service.adminService.AdminService;
import JGS.CasperEvent.global.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // 어드민 생성
    @PostMapping("/join")
    public ResponseEntity<ResponseDto> postAdmin(@RequestBody @Valid AdminRequestDto adminRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.postAdmin(adminRequestDto));
    }

    // 추첨 이벤트 조회
    @GetMapping("/event/lottery")
    public ResponseEntity<List<LotteryEventDetailResponseDto>> getLotteryEvent() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.getLotteryEvent());
    }

    // 추첨 이벤트 생성
    @PostMapping("/event/lottery")
    public ResponseEntity<LotteryEventResponseDto> createLotteryEvent(
            @Valid @RequestBody LotteryEventRequestDto lotteryEventRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.createLotteryEvent(lotteryEventRequestDto));
    }

    // 추첨 이벤트 참여자 조회
    @GetMapping("/event/lottery/participants")
    public ResponseEntity<LotteryEventParticipantsListResponseDto> getLotteryEventParticipants(
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "number", required = false, defaultValue = "") String phoneNumber) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.getLotteryEventParticipants(size, page, phoneNumber));
    }
}
