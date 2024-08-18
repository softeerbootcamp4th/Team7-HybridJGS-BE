package JGS.CasperEvent.domain.event.service.adminService;

import JGS.CasperEvent.domain.event.dto.RequestDto.AdminRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto.LotteryEventRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto.RushEventOptionRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto.RushEventRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.ImageUrlResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventDetailResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventParticipantsListResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventParticipantsResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.AdminRushEventResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.RushEventOptionResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.RushEventParticipantResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.RushEventParticipantsListResponseDto;
import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.entity.event.RushOption;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
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
import JGS.CasperEvent.global.enums.Position;
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
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    private RushEvent rushEvent;
    private RushOption leftOption;
    @Mock
    private RushOption rightOption;

    private Admin admin;
    private BaseUser user1;
    private BaseUser user2;
    private LotteryEvent lotteryEvent;
    private LotteryEventRequestDto lotteryEventRequestDto;
    private LotteryParticipants lotteryParticipants;

    private RushEventRequestDto rushEventRequestDto;
    private RushEventOptionRequestDto leftOptionRequestDto;
    private RushEventOptionRequestDto rightOptionRequestDto;
    private RushParticipants rushParticipant1;
    private RushParticipants rushParticipant2;

    @InjectMocks
    AdminService adminService;

    @BeforeEach
    void setUp() {
        // 어드민 객체
        admin = new Admin("adminId", "password", Role.ADMIN);

        // 유저 객체
        user1 = new BaseUser("010-0000-0000", Role.USER);
        user1.setCreatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        user1.setUpdatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));

        user2 = new BaseUser("010-9999-9999", Role.USER);
        user2.setCreatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        user2.setUpdatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));

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
        lotteryParticipants = new LotteryParticipants(user1);


        // 선착순 이벤트 옵션 요청 DTO
        leftOptionRequestDto = RushEventOptionRequestDto.builder()
                .rushOptionId(1L)
                .position(Position.LEFT)
                .mainText("Main Text 1")
                .subText("Sub Text 1")
                .resultMainText("Result Main Text 1")
                .resultSubText("Result Sub Text 1")
                .imageUrl("http://example.com/leftImage.jpg").build();

        rightOptionRequestDto = RushEventOptionRequestDto.builder()
                .rushOptionId(1L)
                .position(Position.RIGHT)
                .mainText("Main Text 2")
                .subText("Sub Text 2")
                .resultMainText("Result Main Text 2")
                .resultSubText("Result Sub Text 2")
                .imageUrl("http://example.com/rightImage.jpg").build();

        Set<RushEventOptionRequestDto> options = new HashSet<>();
        options.add(leftOptionRequestDto);
        options.add(rightOptionRequestDto);

        // 선착순 이벤트 요청 DTO
        rushEventRequestDto = RushEventRequestDto.builder()
                .rushEventId(1L)
                .eventDate(LocalDate.of(2024, 8, 15))
                .startTime(LocalTime.of(0, 0))
                .endTime(LocalTime.of(23, 59))
                .winnerCount(100)
                .prizeImageUrl("http://example.com/image.jpg")
                .prizeDescription("This is a detailed description of the prize.")
                .options(options)
                .build();

        // 선착순 이벤트 객체
        rushEvent = new RushEvent(
                LocalDateTime.of(rushEventRequestDto.getEventDate(), rushEventRequestDto.getStartTime()),
                LocalDateTime.of(rushEventRequestDto.getEventDate(), rushEventRequestDto.getEndTime()),
                rushEventRequestDto.getWinnerCount(),
                "http://example.com/image.jpg",
                rushEventRequestDto.getPrizeDescription()
        );

        // 선착순 이벤트 선택지 객체
        leftOption = new RushOption(
                rushEvent,
                leftOptionRequestDto.getMainText(),
                leftOptionRequestDto.getSubText(),
                leftOptionRequestDto.getResultMainText(),
                leftOptionRequestDto.getResultSubText(),
                "http://example.com/image.jpg",
                Position.LEFT
        );

        rightOption = new RushOption(
                rushEvent,
                rightOptionRequestDto.getMainText(),
                rightOptionRequestDto.getSubText(),
                rightOptionRequestDto.getResultMainText(),
                rightOptionRequestDto.getResultSubText(),
                "http://example.com/image.jpg",
                Position.RIGHT
        );

        rushEvent.addOption(leftOption, rightOption);

        // 선착순 이벤트 참여자
        rushParticipant1 = new RushParticipants(user1, rushEvent, 1);
        rushParticipant1.setCreatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        rushParticipant1.setUpdatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));

        rushParticipant2 = new RushParticipants(user1, rushEvent, 2);
        rushParticipant2.setCreatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        rushParticipant2.setUpdatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
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
    void postAdminTest_Success() {
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

    @Test
    @DisplayName("추첨 이벤트 참여자 조회 성공 테스트 (전화번호가 있을 때)")
    void getLotteryEventParticipantsTest_Success_withPhoneNumber() {
        //given
        List<LotteryParticipants> lotteryParticipantsList = new ArrayList<>();
        lotteryParticipantsList.add(lotteryParticipants);
        Page<LotteryParticipants> lotteryParticipantsPage = new PageImpl<>(lotteryParticipantsList);


        given(lotteryParticipantsRepository.findByBaseUser_Id(eq("010-0000-0000"), any(Pageable.class))).willReturn(lotteryParticipantsPage);
        given(lotteryParticipantsRepository.countByBaseUser_Id("010-0000-0000")).willReturn(1L);

        //when
        LotteryEventParticipantsListResponseDto lotteryEventParticipantsListResponseDto = adminService.getLotteryEventParticipants(10, 0, "010-0000-0000");
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

    @Test
    @DisplayName("선착순 이벤트 생성 테스트 - 성공")
    void createRushEventTest_Success() {
        //given
        MockMultipartFile prizeImg = new MockMultipartFile("prizeImg", "prizeImage.png", "png", "<<data>>".getBytes());
        MockMultipartFile leftOptionImg = new MockMultipartFile("leftOptionImg", "leftOptionImage.png", "png", "<<data>>".getBytes());
        MockMultipartFile rightOptionImg = new MockMultipartFile("rightOptionImg", "rightOptionImage.png", "png", "<<data>>".getBytes());


        given(rushEventRepository.count()).willReturn(1L);
        given(rushEventRepository.save(rushEvent)).willReturn(rushEvent);
        given(rushOptionRepository.save(leftOption)).willReturn(leftOption);
        given(rushOptionRepository.save(rightOption)).willReturn(rightOption);

        given(s3Service.upload(any())).willReturn("http://example.com/image.jpg");


        //when
        AdminRushEventResponseDto adminRushEventResponseDto = adminService.createRushEvent(rushEventRequestDto, prizeImg, leftOptionImg, rightOptionImg);

        //then
        assertThat(adminRushEventResponseDto.eventDate()).isEqualTo(LocalDate.of(2024, 8, 15));
        assertThat(adminRushEventResponseDto.startTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(adminRushEventResponseDto.endTime()).isEqualTo(LocalTime.of(23, 59));
        assertThat(adminRushEventResponseDto.winnerCount()).isEqualTo(100);
        assertThat(adminRushEventResponseDto.prizeImageUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(adminRushEventResponseDto.prizeDescription()).isEqualTo("This is a detailed description of the prize.");
        assertThat(adminRushEventResponseDto.status()).isEqualTo(EventStatus.AFTER);

        Set<RushEventOptionResponseDto> options = adminRushEventResponseDto.options();

        boolean firstOptionFound = false;
        boolean secondOptionFound = false;

        for (RushEventOptionResponseDto option : options) {
            if (option.mainText().equals("Main Text 2") &&
                    option.subText().equals("Sub Text 2") &&
                    option.resultMainText().equals("Result Main Text 2") &&
                    option.resultSubText().equals("Result Sub Text 2") &&
                    option.imageUrl().equals("http://example.com/image.jpg") &&
                    option.position().equals(Position.RIGHT)) {
                firstOptionFound = true;
            } else if (option.mainText().equals("Main Text 1") &&
                    option.subText().equals("Sub Text 1") &&
                    option.resultMainText().equals("Result Main Text 1") &&
                    option.resultSubText().equals("Result Sub Text 1") &&
                    option.imageUrl().equals("http://example.com/image.jpg") &&
                    option.position().equals(Position.LEFT)) {
                secondOptionFound = true;
            }
        }

        assertThat(firstOptionFound).isTrue();
        assertThat(secondOptionFound).isTrue();
    }

    @Test
    @DisplayName("선착순 이벤트 생성 테스트 - 실패 (데이터베이스에 이미 6개의 선착순 이벤트가 존재할 때)")
    void createRushEventTest_Failure_TooManyRushEvent() {
        //given
        given(rushEventRepository.count()).willReturn(6L);
        MockMultipartFile prizeImg = new MockMultipartFile("prizeImg", "prizeImage.png", "png", "<<data>>".getBytes());
        MockMultipartFile leftOptionImg = new MockMultipartFile("leftOptionImg", "leftOptionImage.png", "png", "<<data>>".getBytes());
        MockMultipartFile rightOptionImg = new MockMultipartFile("rightOptionImg", "rightOptionImage.png", "png", "<<data>>".getBytes());

        //when
        CustomException customException = assertThrows(CustomException.class, () ->
                adminService.createRushEvent(rushEventRequestDto, prizeImg, leftOptionImg, rightOptionImg)
        );

        //then
        assertEquals(CustomErrorCode.TOO_MANY_RUSH_EVENT, customException.getErrorCode());
        assertEquals("현재 진행중인 선착순 이벤트가 6개 이상입니다.", customException.getMessage());
    }

    @Test
    @DisplayName("선착순 이벤트 조회 테스트 - 성공")
    void getRushEventsTest_Success() {
        //given
        List<RushEvent> rushEventList = new ArrayList<>();
        rushEventList.add(rushEvent);
        given(rushEventRepository.findAll()).willReturn(rushEventList);

        //when
        List<AdminRushEventResponseDto> rushEvents = adminService.getRushEvents();

        //then
        AdminRushEventResponseDto firstEvent = rushEvents.get(0);
        assertThat(firstEvent.eventDate()).isEqualTo(LocalDate.of(2024, 8, 15));
        assertThat(firstEvent.startTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(firstEvent.endTime()).isEqualTo(LocalTime.of(23, 59));
        assertThat(firstEvent.winnerCount()).isEqualTo(100);
        assertThat(firstEvent.prizeImageUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(firstEvent.prizeDescription()).isEqualTo("This is a detailed description of the prize.");
        assertThat(firstEvent.status()).isEqualTo(EventStatus.AFTER);

        Set<RushEventOptionResponseDto> options = firstEvent.options();

        boolean firstOptionFound = false;
        boolean secondOptionFound = false;
        for (RushEventOptionResponseDto option : options) {
            if (option.mainText().equals("Main Text 1") &&
                    option.subText().equals("Sub Text 1") &&
                    option.resultMainText().equals("Result Main Text 1") &&
                    option.resultSubText().equals("Result Sub Text 1") &&
                    option.imageUrl().equals("http://example.com/image.jpg") &&
                    option.position().equals(Position.LEFT)) {
                firstOptionFound = true;
            } else if (option.mainText().equals("Main Text 2") &&
                    option.subText().equals("Sub Text 2") &&
                    option.resultMainText().equals("Result Main Text 2") &&
                    option.resultSubText().equals("Result Sub Text 2") &&
                    option.imageUrl().equals("http://example.com/image.jpg") &&
                    option.position().equals(Position.RIGHT)) {
                secondOptionFound = true;
            }
        }

        assertThat(firstOptionFound).isTrue();
        assertThat(secondOptionFound).isTrue();
    }

    @Test
    @DisplayName("선착순 이벤트 참여자 조회 테스트 - 성공 (전화번호가 존재하고 결과가 동점이 아닌 경우")
    void testName() {
        //given
        List<RushParticipants> rushParticipantsList = new ArrayList<>();
        rushParticipantsList.add(rushParticipant1);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushParticipantsRepository.findByRushEvent_RushEventIdAndOptionIdAndBaseUser_Id(eq(1L), eq(1), eq("010-0000-0000"), any(Pageable.class)))
                .willReturn(rushParticipantsPage);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionIdAndBaseUser_Id(1L, 1, "010-0000-0000"))
                .willReturn(1L);

        //when
        RushEventParticipantsListResponseDto rushEventParticipants = adminService.getRushEventParticipants(1, 1, 0, 1, "010-0000-0000");

        //then
        assertThat(rushEventParticipants.isLastPage()).isTrue();
        assertThat(rushEventParticipants.totalParticipants()).isEqualTo(1);

        List<RushEventParticipantResponseDto> participantsList = rushEventParticipants.participantsList();

        RushEventParticipantResponseDto participant = participantsList.get(0);

        assertThat(participant.phoneNumber()).isEqualTo("010-0000-0000");
        assertThat(participant.balanceGameChoice()).isEqualTo(1);
        assertThat(participant.createdDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(participant.createdTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(participant.rank()).isEqualTo(0);

    }
}
