package JGS.CasperEvent.domain.event.service.adminService;

import JGS.CasperEvent.domain.event.dto.RequestDto.AdminRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto.LotteryEventRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.ImageUrlResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventResponseDto;
import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.repository.AdminRepository;
import JGS.CasperEvent.domain.event.repository.CasperBotRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushOptionRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryParticipantsRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryWinnerRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.RushParticipantsRepository;
import JGS.CasperEvent.global.enums.CustomErrorCode;
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
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

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
    private LotteryEvent lotteryEvent;
    private LotteryEventRequestDto lotteryEventRequestDto;

    @BeforeEach
    void setUp() {
        // 어드민 객체
        admin = new Admin("adminId", "password", Role.ADMIN);

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
        assertThat(lotteryEventResponseDto.serverDateTime()).isEqualTo("2024-08-18T13:36:18.155541");
        assertThat(lotteryEventResponseDto.eventStartDate()).isEqualTo("2000-09-27T00:00");
        assertThat(lotteryEventResponseDto.eventEndDate()).isEqualTo("2100-09-27T00:00");
        assertThat(lotteryEventResponseDto.activePeriod()).isEqualTo(36524);
    }
}