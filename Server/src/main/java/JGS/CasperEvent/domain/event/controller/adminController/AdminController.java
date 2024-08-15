package JGS.CasperEvent.domain.event.controller.adminController;

import JGS.CasperEvent.domain.event.dto.RequestDto.AdminRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto.LotteryEventRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto.RushEventRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.ImageUrlResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.*;
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.AdminRushEventOptionResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.AdminRushEventResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.LotteryEventWinnerListResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.RushEventParticipantsListResponseDto;
import JGS.CasperEvent.domain.event.service.adminService.AdminService;
import JGS.CasperEvent.global.response.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // 이미지 업로드
    @PostMapping("/image")
    public ResponseEntity<ImageUrlResponseDto> postImage(@RequestPart(value = "image") MultipartFile image) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.postImage(image));
    }

    // 추첨 이벤트 조회
    @GetMapping("/event/lottery")
    public ResponseEntity<LotteryEventDetailResponseDto> getLotteryEvent() {
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

    // 선착순 이벤트 생성
    @PostMapping("/event/rush")
    public ResponseEntity<AdminRushEventResponseDto> createRushEvent(
            @RequestPart(value = "dto") RushEventRequestDto rushEventRequestDto,
            @RequestPart(value = "prizeImg") MultipartFile prizeImg,
            @RequestPart(value = "leftOptionImg") MultipartFile leftOptionImg,
            @RequestPart(value = "rightOptionImg") MultipartFile rightOptionImg) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.createRushEvent(rushEventRequestDto, prizeImg, leftOptionImg, rightOptionImg));
    }

    // 선착순 이벤트 전체 조회
    @GetMapping("/event/rush")
    public ResponseEntity<List<AdminRushEventResponseDto>> getRushEvents() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.getRushEvents());
    }

    // 선착순 이벤트 참여자 조회
    @GetMapping("/event/rush/{rushEventId}/participants")
    public ResponseEntity<RushEventParticipantsListResponseDto> getRushEventParticipants(
            @PathVariable("rushEventId") Long rushEventId,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "option", required = false, defaultValue = "0") int option,
            @RequestParam(name = "number", required = false, defaultValue = "") String phoneNumber) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.getRushEventParticipants(rushEventId, size, page, option, phoneNumber));
    }

    // 선착순 이벤트 당첨자 조회
    @GetMapping("/event/rush/{rushEventId}/winner")
    public ResponseEntity<RushEventParticipantsListResponseDto> getRushEventWinners(
            @PathVariable("rushEventId") Long rushEventId,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "number", required = false, defaultValue = "") String phoneNumber) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.getRushEventWinners(rushEventId, size, page, phoneNumber));
    }

    // 선착순 이벤트 수정
    @PutMapping("/event/rush")
    public ResponseEntity<List<AdminRushEventResponseDto>> updateRushEvent(
            @RequestBody List<RushEventRequestDto> rushEventListRequestDto) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.updateRushEvents(rushEventListRequestDto));
    }

    // 선착순 이벤트 삭제
    @DeleteMapping("/event/rush/{rushEventId}")
    public ResponseEntity<ResponseDto> deleteRushEvent(@PathVariable Long rushEventId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.deleteRushEvent(rushEventId));
    }

    // 선착순 이벤트 선택지 조회
    @GetMapping("/event/rush/{rushEventId}/options")
    public ResponseEntity<AdminRushEventOptionResponseDto> getRushEventOptions(@PathVariable Long rushEventId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.getRushEventOptions(rushEventId));
    }

    // 추첨 이벤트 삭제
    @DeleteMapping("/event/lottery")
    public ResponseEntity<Void> deleteLotteryEvent() {
        adminService.deleteLotteryEvent();
        return ResponseEntity.noContent().build(); // 204 No Content

    }

    // 추첨 이벤트 수정
    @PutMapping("/event/lottery")
    public ResponseEntity<LotteryEventDetailResponseDto> updateLotteryEvent(@RequestBody @Valid LotteryEventRequestDto lotteryEventRequestDto) {
        LotteryEventDetailResponseDto updatedLotteryEventDetailResponseDto = adminService.updateLotteryEvent(lotteryEventRequestDto);

        return ResponseEntity.ok(updatedLotteryEventDetailResponseDto);
    }

    // 추첨 이벤트 특정 사용자의 기대평 조회
    @GetMapping("/event/lottery/participants/{participantId}/expectations")
    public ResponseEntity<LotteryEventExpectationsResponseDto> getLotteryEventExpectations(@PathVariable("participantId") Long participantId,
                                                                                                @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                                                                @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        LotteryEventExpectationsResponseDto lotteryEventExpectationResponseDtoList = adminService.getLotteryEventExpectations(page, size, participantId);

        return ResponseEntity.ok(lotteryEventExpectationResponseDtoList);
    }

    // 추첨 이벤트 특정 기대평을 삭제
    @PatchMapping("/event/lottery/expectations/{casperId}")
    public ResponseEntity<Void> deleteLotteryEventExpectation(@PathVariable("casperId") Long casperId) {
        adminService.deleteLotteryEventExpectation(casperId);

        return ResponseEntity.noContent().build();
    }

    // 추첨 이벤트 당첨자 추첨
    @PostMapping("/event/lottery/winner")
    public ResponseEntity<ResponseDto> pickLotteryEventWinners() {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.pickLotteryEventWinners());
    }

    // 추첨 이벤트 당첨자 조회
    @GetMapping("/event/lottery/winner")
    public ResponseEntity<LotteryEventWinnerListResponseDto> getWinners(
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "number", required = false, defaultValue = "") String phoneNumber) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.getLotteryEventWinners(size, page, phoneNumber));
    }
}
