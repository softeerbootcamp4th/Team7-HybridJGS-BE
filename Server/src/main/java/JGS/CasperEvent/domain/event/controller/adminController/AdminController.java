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
import JGS.CasperEvent.domain.event.dto.response.LotteryEventResponseDto;
import JGS.CasperEvent.domain.event.service.adminService.AdminService;
import JGS.CasperEvent.global.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "관리자 API", description = "관리자 관련 API 목록입니다.")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "어드민 객체 생성", description = "아이디와 비밀번호를 이용해 어드민 객체를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin account created successfully."),
            @ApiResponse(responseCode = "409", description = "The ID is already in use.")
    })
    @PostMapping("/join")
    public ResponseEntity<ResponseDto> postAdmin(@RequestBody @Valid AdminRequestDto adminRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.postAdmin(adminRequestDto));
    }

    @Operation(summary = "이미지 업로드", description = "AWS S3 버킷 이미지 업로드 요청입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully."),
            @ApiResponse(responseCode = "400", description = "Failed to upload image.")
    })
    @PostMapping("/image")
    public ResponseEntity<ImageUrlResponseDto> postImage(@RequestPart(value = "image") MultipartFile image) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.postImage(image));
    }

    @Operation(summary = "추첨 이벤트 조회", description = "현재 데이터베이스에 존재하는 추첨 이벤트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lottery event retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "No lottery event found in the database."),
            @ApiResponse(responseCode = "409", description = "Multiple lottery events found in the database.")
    })
    @GetMapping("/event/lottery")
    public ResponseEntity<LotteryEventResponseDto> getLotteryEvent() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.getLotteryEvent());
    }

    @Operation(summary = "추첨 이벤트 생성", description = "이벤트 시작 날짜, 종료 날짜, 당첨인원을 통해 추첨 이벤트를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lottery event created successfully."),
            @ApiResponse(responseCode = "409", description = "A lottery event already exists in the database.")
    })
    @PostMapping("/event/lottery")
    public ResponseEntity<LotteryEventResponseDto> createLotteryEvent(
            @Valid @RequestBody LotteryEventRequestDto lotteryEventRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.createLotteryEvent(lotteryEventRequestDto));
    }

    @Operation(summary = "추첨 이벤트 참여자 조회", description = "추첨 이벤트 참여자를 조회합니다. size, page를 통해 페이지네이션이 가능하며, 전화번호를 통해 검색할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "Lottery event participants retrieved successfully.")
    @GetMapping("/event/lottery/participants")
    public ResponseEntity<LotteryEventParticipantsListResponseDto> getLotteryEventParticipants(
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "number", required = false, defaultValue = "") String phoneNumber) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.getLotteryEventParticipants(size, page, phoneNumber));
    }

    @Operation(summary = "선착순 이벤트 생성", description = "선착순 이벤트와 선택지 정보를 통해 선착순 이벤트를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rush event created successfully."),
            @ApiResponse(responseCode = "409", description = "More than six rush events already exist in the database.")
    })
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

    @Operation(summary = "선착순 이벤트 조회", description = "현재 데이터베이스에 존재하는 전체 선착순 이벤트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "Rush events retrieved successfully.")
    @GetMapping("/event/rush")
    public ResponseEntity<List<AdminRushEventResponseDto>> getRushEvents() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.getRushEvents());
    }

    @Operation(summary = "선착순 이벤트 참여자 조회", description = "선착순 이벤트 참여자를 조회합니다. rushEventId가 필요합니다. size, page를 통해 페이지네이션이 가능하며, 전화번호를 통해 검색할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "Rush event participants retrieved successfully.")
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

    @Operation(summary = "선착순 이벤트 당첨자 조회", description = "선착순 이벤트 당첨자를 조회합니다. rushEventId가 필요합니다. size, page를 통해 페이지네이션이 가능하며, 전화번호를 통해 검색할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "Rush event winners retrieved successfully.")
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

    @Operation(summary = "선착순 이벤트 수정", description = "선착순 이벤트 정보를 통해 이벤트를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rush event updated successfully."),
            @ApiResponse(responseCode = "400", description = "Failed to update rush event.")
    })
    @PutMapping("/event/rush")
    public ResponseEntity<List<AdminRushEventResponseDto>> updateRushEvent(
            @RequestBody List<RushEventRequestDto> rushEventListRequestDto) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.updateRushEvents(rushEventListRequestDto));
    }

    @Operation(summary = "선착순 이벤트를 삭제", description = "rushEventId를 통해 선착순 이벤트를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rush event deleted successfully."),
            @ApiResponse(responseCode = "404", description = "No rush event found matching the provided ID.")
    })
    @DeleteMapping("/event/rush/{rushEventId}")
    public ResponseEntity<ResponseDto> deleteRushEvent(@PathVariable Long rushEventId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.deleteRushEvent(rushEventId));
    }

    @Operation(summary = "선착순 이벤트 선택지 조회", description = "rushEventId를 통해 선착순 이벤트의 선택지를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rush event options retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "No rush event found matching the provided ID.")
    })
    @GetMapping("/event/rush/{rushEventId}/options")
    public ResponseEntity<AdminRushEventOptionResponseDto> getRushEventOptions(@PathVariable Long rushEventId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.getRushEventOptions(rushEventId));
    }

    @Operation(summary = "선착순 이벤트 삭제", description = "현재 진행중인 선착순 이벤트를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "Ongoing rush event deleted successfully.")
    @DeleteMapping("/event/lottery")
    public ResponseEntity<Void> deleteLotteryEvent() {
        adminService.deleteLotteryEvent();
        return ResponseEntity.noContent().build(); // 204 No Content

    }

    @Operation(summary = "추첨 이벤트 수정", description = "추첨 이벤트 정보를 통해 이벤트를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lottery event updated successfully."),
            @ApiResponse(responseCode = "400", description = "Failed to update lottery event.")
    })
    @PutMapping("/event/lottery")
    public ResponseEntity<LotteryEventDetailResponseDto> updateLotteryEvent(@RequestBody @Valid LotteryEventRequestDto lotteryEventRequestDto) {
        LotteryEventDetailResponseDto updatedLotteryEventDetailResponseDto = adminService.updateLotteryEvent(lotteryEventRequestDto);

        return ResponseEntity.ok(updatedLotteryEventDetailResponseDto);
    }

    @Operation(summary = "추첨 이벤트 기대평 조회", description = "participantId를 통해 특정 참가자의 기대평을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lottery event expectations retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "User not found or did not participate.")
    })
    @GetMapping("/event/lottery/participants/{participantId}/expectations")
    public ResponseEntity<LotteryEventExpectationsResponseDto> getLotteryEventExpectations(@PathVariable("participantId") Long participantId,
                                                                                           @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                                                           @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        LotteryEventExpectationsResponseDto lotteryEventExpectationResponseDtoList = adminService.getLotteryEventExpectations(page, size, participantId);

        return ResponseEntity.ok(lotteryEventExpectationResponseDtoList);
    }

    @Operation(summary = "추첨 이벤트 기대평 삭제", description = "casperId를 통해 기대평을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lottery event expectation deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Casper bot with the provided casperId not found.")
    })
    @PatchMapping("/event/lottery/expectations/{casperId}")
    public ResponseEntity<Void> deleteLotteryEventExpectation(@PathVariable("casperId") Long casperId) {
        adminService.deleteLotteryEventExpectation(casperId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "추첨 이벤트 당첨자 추첨", description = "추첨 이벤트의 당첨자를 추첨합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Winners picked successfully."),
            @ApiResponse(responseCode = "409", description = "Winners have already been picked for this lottery event.")
    })
    @PostMapping("/event/lottery/winner")
    public ResponseEntity<ResponseDto> pickLotteryEventWinners() {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.pickLotteryEventWinners());
    }

    @Operation(summary = "추첨 이벤트 당첨자 초기화", description = "추첨 이벤트의 당첨자 테이블을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "Lottery event winners reset successfully.")
    @DeleteMapping("/event/lottery/winner")
    public ResponseEntity<ResponseDto> deleteLotteryEventWinners() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.deleteLotteryEventWinners());
    }

    @Operation(summary = "추첨 이벤트 당첨자 조회", description = "추첨 이벤트의 당첨자를 조회합니다. size, page를 통해 페이지네이션이 가능하며, 전화번호를 통해 검색할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lottery event winners retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Lottery event has not yet been drawn.")
    })
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
