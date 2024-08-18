package JGS.CasperEvent.domain.event.service.adminService;

import JGS.CasperEvent.domain.event.dto.RequestDto.AdminRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto.LotteryEventRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.ImageUrlResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventDetailResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventParticipantsListResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventParticipantsResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventResponseDto;
import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.repository.AdminRepository;
import JGS.CasperEvent.domain.event.repository.CasperBotRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushOptionRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryParticipantsRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryWinnerRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.RushParticipantsRepository;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.enums.EventStatus;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.response.ResponseDto;
import JGS.CasperEvent.global.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;
    @Mock
    private LotteryEventRepository lotteryEventRepository;
    @Mock
    private RushEventRepository rushEventRepository;
    @Mock
    private LotteryParticipantsRepository lotteryParticipantsRepository;
    @Mock
    private RushParticipantsRepository rushParticipantsRepository;
    @Mock
    private RushOptionRepository rushOptionRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private CasperBotRepository casperBotRepository;
    @Mock
    private LotteryWinnerRepository lotteryWinnerRepository;

    @InjectMocks
    AdminService adminService;

    private Admin admin;

    private BaseUser user;
    private LotteryEvent lotteryEvent;
    private LotteryEventRequestDto lotteryEventRequestDto;
    private LotteryParticipants lotteryParticipants;

    @BeforeEach
    void setUp() {
        // 어드민 객체
        admin = new Admin("adminId", "password", Role.ADMIN);

        // 유저 객체
        user = new BaseUser("010-0000-0000", Role.USER);
        user.setCreatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        user.setUpdatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        // 추첨 이벤트 생성 요청 DTO
        lotteryEventRequestDto = LotteryEventRequestDto.builder()
                .startDate(LocalDate.of(2000, 9, 27))
                .startTime(LocalTime.of(0, 0))
                .endDate(LocalDate.of(2100, 9, 27))
                .endTime(LocalTime.of(0, 0))
                .winnerCount(315)
                .build();

        // 추첨 이벤트 엔티티
        lotteryEvent = new LotteryEvent(
                LocalDateTime.of(lotteryEventRequestDto.getStartDate(), lotteryEventRequestDto.getStartTime()),
                LocalDateTime.of(lotteryEventRequestDto.getEndDate(), lotteryEventRequestDto.getEndTime()),
                lotteryEventRequestDto.getWinnerCount()
        );

        // 추첨 이벤트 참여자 엔티티
        lotteryParticipants = new LotteryParticipants(user);
    }

    @Test
    @DisplayName("어드민 인증 테스트 - 성공")
    void verifyAdminTest_Success() {
        //given
        AdminRequestDto adminRequestDto = AdminRequestDto.builder()
                .adminId("adminId")
                .password("password")
                .build();

        given(adminRepository.findByIdAndPassword("adminId", "password")).willReturn(Optional.ofNullable(admin));

        //when
        Admin admin = adminService.verifyAdmin(adminRequestDto);

        //then
        assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
        assertThat(admin.getId()).isEqualTo("adminId");
        assertThat(admin.getPassword()).isEqualTo("password");
    }

    @Test
    @DisplayName("어드민 생성 테스트 - 성공")
    void testName() {
        //given
        AdminRequestDto adminRequestDto = AdminRequestDto.builder()
                .adminId("adminId")
                .password("password")
                .build();

        //when
        ResponseDto responseDto = adminService.postAdmin(adminRequestDto);

        //then
        assertThat(responseDto.message()).isEqualTo("관리자 생성 성공");
    }

    @Test
    @DisplayName("어드민 생성 테스트 - 실패 (중복 아이디 존재)")
    void postAdminTest_Failure_DuplicatedId() {
        //given
        AdminRequestDto adminRequestDto = AdminRequestDto.builder()
                .adminId("adminId")
                .password("password")
                .build();

        given(adminRepository.findById("adminId")).willReturn(Optional.ofNullable(admin));

        //when
        CustomException customException = assertThrows(CustomException.class, () ->
                adminService.postAdmin(adminRequestDto)
        );

        //then
        assertEquals(CustomErrorCode.CONFLICT, customException.getErrorCode());
        assertEquals("이미 등록된 ID입니다.", customException.getMessage());
    }

    @Test
    @DisplayName("이미지 업로드 성공 테스트")
    void postImageTest_Success() {
        //given
        MockMultipartFile image = new MockMultipartFile("image", "image.png", "png", "<<data>>".getBytes());
        given(s3Service.upload(image)).willReturn("www.image.com");

        //when
        ImageUrlResponseDto imageUrlResponseDto = adminService.postImage(image);

        //then
        assertThat(imageUrlResponseDto.imageUrl()).isEqualTo("www.image.com");
    }

    @Test
    @DisplayName("추첨 이벤트 생성 테스트 - 성공")
    void createLotteryEventTest_Success() {
        //given
        given(lotteryEventRepository.count()).willReturn(0L);
        given(lotteryEventRepository.save(lotteryEvent)).willReturn(lotteryEvent);

        //when
        LotteryEventResponseDto lotteryEventResponseDto = adminService.createLotteryEvent(lotteryEventRequestDto);

        //then
        assertThat(lotteryEventResponseDto.serverDateTime()).isNotNull();
        assertThat(lotteryEventResponseDto.eventStartDate()).isEqualTo("2000-09-27T00:00");
        assertThat(lotteryEventResponseDto.eventEndDate()).isEqualTo("2100-09-27T00:00");
        assertThat(lotteryEventResponseDto.activePeriod()).isEqualTo(36524);
    }

    @Test
    @DisplayName("추첨 이벤트 생성 테스트 - 실패 (데이터베이스에 추첨 이벤트가 존재할 때)")
    void createLotteryEventTest_Failure_TooManyLotteryEvent() {
        //given
        given(lotteryEventRepository.count()).willReturn(1L);

        //when
        CustomException customException = assertThrows(CustomException.class, () ->
                adminService.createLotteryEvent(lotteryEventRequestDto)
        );

        //then
        assertEquals(CustomErrorCode.TOO_MANY_LOTTERY_EVENT, customException.getErrorCode());
        assertEquals("현재 진행중인 추첨 이벤트가 2개 이상입니다.", customException.getMessage());
    }

    @Test
    @DisplayName("추첨 이벤트 조회 성공 테스트")
    void getLotteryEventTest_Success() {
        //given
        List<LotteryEvent> lotteryEventList = new ArrayList<>();
        lotteryEventList.add(lotteryEvent);
        given(lotteryEventRepository.findAll()).willReturn(lotteryEventList);

        //when
        LotteryEventDetailResponseDto lotteryEventDetailResponseDto = adminService.getLotteryEvent();

        //then
        assertThat(lotteryEventDetailResponseDto.startDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(lotteryEventDetailResponseDto.startTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(lotteryEventDetailResponseDto.endDate()).isEqualTo(LocalDate.of(2100, 9, 27));
        assertThat(lotteryEventDetailResponseDto.endTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(lotteryEventDetailResponseDto.appliedCount()).isEqualTo(0);
        assertThat(lotteryEventDetailResponseDto.winnerCount()).isEqualTo(315);
        assertThat(lotteryEventDetailResponseDto.status()).isEqualTo(EventStatus.DURING);
    }

    @Test
    @DisplayName("추첨 이벤트 참여자 조회 성공 테스트 (전화번호가 없을 때)")
    void getLotteryEventParticipantsTest_Success_withoutPhoneNumber() {
        //given
        List<LotteryParticipants> lotteryParticipantsList = new ArrayList<>();
        lotteryParticipantsList.add(lotteryParticipants);
        Page<LotteryParticipants> lotteryParticipantsPage = new PageImpl<>(lotteryParticipantsList);


        given(lotteryParticipantsRepository.findAll(any(Pageable.class))).willReturn(lotteryParticipantsPage);
        given(lotteryParticipantsRepository.count()).willReturn(1L);

        //when
        LotteryEventParticipantsListResponseDto lotteryEventParticipantsListResponseDto = adminService.getLotteryEventParticipants(10, 0, "");
        LotteryEventParticipantsResponseDto retrievedParticipant = lotteryEventParticipantsListResponseDto.participantsList().get(0);

        //then
        assertThat(lotteryEventParticipantsListResponseDto.isLastPage()).isTrue();
        assertThat(lotteryEventParticipantsListResponseDto.totalParticipants()).isEqualTo(1);

        assertThat(retrievedParticipant.phoneNumber()).isEqualTo("010-0000-0000");
        assertThat(retrievedParticipant.linkClickedCounts()).isEqualTo(0);
        assertThat(retrievedParticipant.expectation()).isEqualTo(0);
        assertThat(retrievedParticipant.appliedCount()).isEqualTo(1);
        assertThat(retrievedParticipant.createdDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(retrievedParticipant.createdTime()).isEqualTo(LocalTime.of(0, 0, 0));
    }
}
