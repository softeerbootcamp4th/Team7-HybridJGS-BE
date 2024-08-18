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
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.*;
import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.entity.event.RushOption;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.entity.participants.LotteryWinners;
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
        lotteryParticipants.setCreatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        lotteryParticipants.setUpdatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));

        // 선착순 이벤트 옵션 요청 DTO
        leftOptionRequestDto = RushEventOptionRequestDto.builder()
                .rushOptionId(1L)
                .position(Position.LEFT)
                .mainText("Main Text 1")
                .subText("Sub Text 1")
                .resultMainText("Result Main Text 1")
                .resultSubText("Result Sub Text 1")
                .imageUrl("http://example.com/image.jpg").build();

        rightOptionRequestDto = RushEventOptionRequestDto.builder()
                .rushOptionId(1L)
                .position(Position.RIGHT)
                .mainText("Main Text 2")
                .subText("Sub Text 2")
                .resultMainText("Result Main Text 2")
                .resultSubText("Result Sub Text 2")
                .imageUrl("http://example.com/image.jpg").build();

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
    @DisplayName("추첨 이벤트 조회 테스트 - 실패 (데이터베이스에 추첨 이벤트가 없을 때)")
    void getLotteryEvent_Failure_NoLotteryEvent() {
        //given
        List<LotteryEvent> lotteryEventList = new ArrayList<>();
        given(lotteryEventRepository.findAll())
                .willReturn(lotteryEventList);

        //when
        CustomException customException = assertThrows(CustomException.class, () ->
                adminService.getLotteryEvent()
        );

        //then
        assertEquals(CustomErrorCode.NO_LOTTERY_EVENT, customException.getErrorCode());
        assertEquals("현재 진행중인 lotteryEvent가 존재하지 않습니다.", customException.getMessage());
    }

    @Test
    @DisplayName("추첨 이벤트 조회 테스트 - 실패 (데이터베이스에 추첨 이벤트가 2개 이상 존재)")
    void getLotteryEvent_Failure_TooManyLotteryEvent() {
        //given
        List<LotteryEvent> lotteryEventList = new ArrayList<>();
        lotteryEventList.add(lotteryEvent);
        lotteryEventList.add(lotteryEvent);
        given(lotteryEventRepository.findAll())
                .willReturn(lotteryEventList);

        //when
        CustomException customException = assertThrows(CustomException.class, () ->
                adminService.getLotteryEvent()
        );

        //then
        assertEquals(CustomErrorCode.TOO_MANY_LOTTERY_EVENT, customException.getErrorCode());
        assertEquals("현재 진행중인 lotteryEvent가 2개 이상입니다.", customException.getMessage());
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
        rushEvent.addOption(leftOption, rightOption);
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
    void getRushEventParticipantsTest_Success_withPhoneNumberAndOptionId() {
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

    @Test
    @DisplayName("선착순 이벤트 참여자 조회 테스트 - 성공 (전화번호가 존재하지 않고 결과가 동점인 경우")
    void getRushEventParticipantsTest_Success_withoutPhoneNumberAndOptionId() {
        //given
        List<RushParticipants> rushParticipantsList = new ArrayList<>();
        rushParticipantsList.add(rushParticipant1);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushParticipantsRepository.findByRushEvent_RushEventId(eq(1L), any(Pageable.class)))
                .willReturn(rushParticipantsPage);
        given(rushParticipantsRepository.countByRushEvent_RushEventId(1L))
                .willReturn(1L);

        //when
        RushEventParticipantsListResponseDto rushEventParticipants = adminService.getRushEventParticipants(1, 1, 0, 0, "");

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

    @Test
    @DisplayName("선착순 이벤트 참여자 조회 테스트 - 성공 (전화번호가 존재하지 않고 결과가 동점이 아닌 경우")
    void getRushEventParticipantsTest_Success_withoutPhoneNumberWithOptionId() {
        //given
        List<RushParticipants> rushParticipantsList = new ArrayList<>();
        rushParticipantsList.add(rushParticipant1);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushParticipantsRepository.findByRushEvent_RushEventIdAndOptionId(eq(1L), eq(1), any(Pageable.class)))
                .willReturn(rushParticipantsPage);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1))
                .willReturn(1L);

        //when
        RushEventParticipantsListResponseDto rushEventParticipants = adminService.getRushEventParticipants(1, 1, 0, 1, "");

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

    @Test
    @DisplayName("선착순 이벤트 참여자 조회 테스트 - 성공 (전화번호가 존재하고 결과가 동점인 경우")
    void getRushEventParticipantsTest_Success_witPhoneNumberAndWithoutOptionId() {
        //given
        List<RushParticipants> rushParticipantsList = new ArrayList<>();
        rushParticipantsList.add(rushParticipant1);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushParticipantsRepository.findByRushEvent_RushEventIdAndBaseUser_Id(eq(1L), eq("010-0000-0000"), any(Pageable.class)))
                .willReturn(rushParticipantsPage);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndBaseUser_Id(1L, "010-0000-0000"))
                .willReturn(1L);

        //when
        RushEventParticipantsListResponseDto rushEventParticipants = adminService.getRushEventParticipants(1, 1, 0, 0, "010-0000-0000");

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

    @Test
    @DisplayName("선착순 이벤트 당첨자 조회 테스트 - 성공 (전화번호가 존재하고 결과가 동점이 아닌 경우")
    void getRushEventWinnersTest_Success_withPhoneNumberAndOptionId() {
        //given
        List<RushParticipants> rushParticipantsList = new ArrayList<>();
        rushParticipantsList.add(rushParticipant1);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushEventRepository.findById(1L)).willReturn(Optional.of(rushEvent));
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1))
                .willReturn(2L);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 2))
                .willReturn(1L);
        given(rushParticipantsRepository.findWinnerByEventIdAndOptionIdAndPhoneNumber(eq(1L), eq(1), eq("010-0000-0000"), any(Pageable.class)))
                .willReturn(rushParticipantsPage);

        //when
        RushEventParticipantsListResponseDto rushEventWinners
                = adminService.getRushEventWinners(1L, 1, 0, "010-0000-0000");

        //then
        assertThat(rushEventWinners.isLastPage()).isTrue();
        assertThat(rushEventWinners.totalParticipants()).isEqualTo(1);

        List<RushEventParticipantResponseDto> participantsList = rushEventWinners.participantsList();

        RushEventParticipantResponseDto participant = participantsList.get(0);

        assertThat(participant.phoneNumber()).isEqualTo("010-0000-0000");
        assertThat(participant.balanceGameChoice()).isEqualTo(1);
        assertThat(participant.createdDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(participant.createdTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(participant.rank()).isEqualTo(0);
    }

    @Test
    @DisplayName("선착순 이벤트 당첨자 조회 테스트 - 성공 (전화번호가 존재하지 않고 결과가 동점인 경우")
    void getRushEventWinnersTest_Success_withoutPhoneNumberAndOptionId() {
        //given
        List<RushParticipants> rushParticipantsList = new ArrayList<>();
        rushParticipantsList.add(rushParticipant1);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushEventRepository.findById(1L)).willReturn(Optional.of(rushEvent));
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1))
                .willReturn(1L);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 2))
                .willReturn(1L);
        given(rushParticipantsRepository.findWinnerByEventId(eq(1L), any(Pageable.class)))
                .willReturn(rushParticipantsPage);

        //when
        RushEventParticipantsListResponseDto rushEventWinners
                = adminService.getRushEventWinners(1L, 1, 0, "");

        //then
        assertThat(rushEventWinners.isLastPage()).isTrue();
        assertThat(rushEventWinners.totalParticipants()).isEqualTo(1);

        List<RushEventParticipantResponseDto> participantsList = rushEventWinners.participantsList();

        RushEventParticipantResponseDto participant = participantsList.get(0);

        assertThat(participant.phoneNumber()).isEqualTo("010-0000-0000");
        assertThat(participant.balanceGameChoice()).isEqualTo(1);
        assertThat(participant.createdDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(participant.createdTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(participant.rank()).isEqualTo(0);
    }

    @Test
    @DisplayName("선착순 이벤트 당첨자 조회 테스트 - 성공 (전화번호가 존재하지 않고 결과가 동점이 아닌 경우")
    void getRushEventWinnersTest_Success_withoutPhoneNumberAndWithOptionId() {
        //given
        List<RushParticipants> rushParticipantsList = new ArrayList<>();
        rushParticipantsList.add(rushParticipant1);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushEventRepository.findById(1L)).willReturn(Optional.of(rushEvent));
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1))
                .willReturn(2L);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 2))
                .willReturn(1L);
        given(rushParticipantsRepository.findWinnerByEventIdAndOptionId(eq(1L), eq(1), any(Pageable.class)))
                .willReturn(rushParticipantsPage);

        //when
        RushEventParticipantsListResponseDto rushEventWinners
                = adminService.getRushEventWinners(1L, 1, 0, "");

        //then
        assertThat(rushEventWinners.isLastPage()).isTrue();
        assertThat(rushEventWinners.totalParticipants()).isEqualTo(1);

        List<RushEventParticipantResponseDto> participantsList = rushEventWinners.participantsList();

        RushEventParticipantResponseDto participant = participantsList.get(0);

        assertThat(participant.phoneNumber()).isEqualTo("010-0000-0000");
        assertThat(participant.balanceGameChoice()).isEqualTo(1);
        assertThat(participant.createdDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(participant.createdTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(participant.rank()).isEqualTo(0);
    }

    @Test
    @DisplayName("선착순 이벤트 당첨자 조회 테스트 - 성공 (전화번호가 존재하고 결과가 동점인 경우")
    void getRushEventWinnersTest_Success_withPhoneNumberAndWithoutOptionId() {
        //given
        List<RushParticipants> rushParticipantsList = new ArrayList<>();
        rushParticipantsList.add(rushParticipant1);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushEventRepository.findById(1L)).willReturn(Optional.of(rushEvent));
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1))
                .willReturn(1L);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 2))
                .willReturn(1L);
        given(rushParticipantsRepository.findByWinnerByEventIdAndPhoneNumber(eq(1L), eq("010-0000-0000"), any(Pageable.class)))
                .willReturn(rushParticipantsPage);

        //when
        RushEventParticipantsListResponseDto rushEventWinners
                = adminService.getRushEventWinners(1L, 1, 0, "010-0000-0000");

        //then
        assertThat(rushEventWinners.isLastPage()).isTrue();
        assertThat(rushEventWinners.totalParticipants()).isEqualTo(1);

        List<RushEventParticipantResponseDto> participantsList = rushEventWinners.participantsList();

        RushEventParticipantResponseDto participant = participantsList.get(0);

        assertThat(participant.phoneNumber()).isEqualTo("010-0000-0000");
        assertThat(participant.balanceGameChoice()).isEqualTo(1);
        assertThat(participant.createdDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(participant.createdTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(participant.rank()).isEqualTo(0);
    }

    @Test
    @DisplayName("선착순 이벤트 삭제 - 성공")
    void deleteLotteryEvent_Success() {
        //given
        List<LotteryEvent> lotteryEventList = new ArrayList<>();
        lotteryEventList.add(lotteryEvent);
        given(lotteryEventRepository.findAll()).willReturn(lotteryEventList);

        //when
        adminService.deleteLotteryEvent();
    }

    @Test
    @DisplayName("추첨 이벤트 업데이트 테스트 - 성공")
    void updateLotteryEventTest_Success() {
        //given
        List<LotteryEvent> lotteryEventList = new ArrayList<>();
        lotteryEventList.add(lotteryEvent);
        given(lotteryEventRepository.findAll()).willReturn(lotteryEventList);

        //when
        LotteryEventDetailResponseDto lotteryEventDetailResponseDto = adminService.updateLotteryEvent(lotteryEventRequestDto);

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
    @DisplayName("선착순 이벤트 업데이트 테스트 - 실패 (종료 날짜가 시작 날짜보다 앞서는 경우)")
    void updateLotteryEventTest_Failure_EndBeforeStart() {
        //given
        List<LotteryEvent> lotteryEventList = new ArrayList<>();
        lotteryEventList.add(lotteryEvent);
        given(lotteryEventRepository.findAll()).willReturn(lotteryEventList);

        lotteryEventRequestDto = LotteryEventRequestDto.builder()
                .startDate(LocalDate.of(2100, 9, 27))
                .startTime(LocalTime.of(0, 0))
                .endDate(LocalDate.of(2000, 9, 27))
                .endTime(LocalTime.of(0, 0))
                .winnerCount(315)
                .build();
        //when
        CustomException customException = assertThrows(CustomException.class, () ->
                adminService.updateLotteryEvent(lotteryEventRequestDto)
        );

        //then
        assertEquals(CustomErrorCode.EVENT_END_TIME_BEFORE_START_TIME, customException.getErrorCode());
        assertEquals("종료 시간은 시작 시간 이후로 설정해야 합니다.", customException.getMessage());
    }

    @Test
    @DisplayName("선착순 이벤트 업데이트 테스트 - 실패 (이벤트가 진행중일 때, 시작 날짜를 수정하는 경우)")
    void updateLotteryEventTest_Failure_ModifyingOngoingEvent() {
        //given
        List<LotteryEvent> lotteryEventList = new ArrayList<>();
        lotteryEventList.add(lotteryEvent);
        given(lotteryEventRepository.findAll()).willReturn(lotteryEventList);

        lotteryEventRequestDto = LotteryEventRequestDto.builder()
                .startDate(LocalDate.of(2050, 9, 27))
                .startTime(LocalTime.of(0, 0))
                .endDate(LocalDate.of(2100, 9, 27))
                .endTime(LocalTime.of(0, 0))
                .winnerCount(315)
                .build();
        //when
        CustomException customException = assertThrows(CustomException.class, () ->
                adminService.updateLotteryEvent(lotteryEventRequestDto)
        );

        //then
        assertEquals(CustomErrorCode.EVENT_IN_PROGRESS_CANNOT_CHANGE_START_TIME, customException.getErrorCode());
        assertEquals("현재 진행 중인 이벤트의 시작 시간을 변경할 수 없습니다.", customException.getMessage());
    }

    @Test
    @DisplayName("선착순 이벤트 업데이트 테스트 - 실패 (이벤트가 진행중일 때, 종료 날짜가 현재 시간보다 앞서는 경우)")
    void updateLotteryEventTest_Failure_EndBeforeNow() {
        //given
        List<LotteryEvent> lotteryEventList = new ArrayList<>();
        lotteryEventList.add(lotteryEvent);
        given(lotteryEventRepository.findAll()).willReturn(lotteryEventList);

        lotteryEventRequestDto = LotteryEventRequestDto.builder()
                .startDate(LocalDate.of(2000, 9, 27))
                .startTime(LocalTime.of(0, 0))
                .endDate(LocalDate.of(2001, 9, 27))
                .endTime(LocalTime.of(0, 0))
                .winnerCount(315)
                .build();
        //when
        CustomException customException = assertThrows(CustomException.class, () ->
                adminService.updateLotteryEvent(lotteryEventRequestDto)
        );

        //then
        assertEquals(CustomErrorCode.EVENT_IN_PROGRESS_END_TIME_BEFORE_NOW, customException.getErrorCode());
        assertEquals("현재 진행 중인 이벤트의 종료 시간을 현재 시간보다 이전으로 설정할 수 없습니다.", customException.getMessage());
    }

    @Test
    @DisplayName("선착순 이벤트 업데이트 테스트 - 실패 (이벤트가 시작 전일 때, 시작 날짜가 현재 시간보다 앞서는 경우)")
    void updateLotteryEventTest_Failure_StartBeforeNow() {
        //given
        lotteryEventRequestDto = LotteryEventRequestDto.builder()
                .startDate(LocalDate.of(2100, 9, 27))
                .startTime(LocalTime.of(0, 0))
                .endDate(LocalDate.of(2200, 9, 27))
                .endTime(LocalTime.of(0, 0))
                .winnerCount(315)
                .build();

        lotteryEvent = new LotteryEvent(
                LocalDateTime.of(lotteryEventRequestDto.getStartDate(), lotteryEventRequestDto.getStartTime()),
                LocalDateTime.of(lotteryEventRequestDto.getEndDate(), lotteryEventRequestDto.getEndTime()),
                lotteryEventRequestDto.getWinnerCount()
        );

        List<LotteryEvent> lotteryEventList = new ArrayList<>();
        lotteryEventList.add(lotteryEvent);
        given(lotteryEventRepository.findAll()).willReturn(lotteryEventList);

        lotteryEventRequestDto = LotteryEventRequestDto.builder()
                .startDate(LocalDate.of(2000, 9, 27))
                .startTime(LocalTime.of(0, 0))
                .endDate(LocalDate.of(2200, 9, 27))
                .endTime(LocalTime.of(0, 0))
                .winnerCount(315)
                .build();
        //when
        CustomException customException = assertThrows(CustomException.class, () ->
                adminService.updateLotteryEvent(lotteryEventRequestDto)
        );

        //then
        assertEquals(CustomErrorCode.EVENT_BEFORE_START_TIME, customException.getErrorCode());
        assertEquals("이벤트 시작 시간은 현재 시간 이후로 설정해야 합니다.", customException.getMessage());
    }

    @Test
    @DisplayName("추첨 이벤트 당첨자 추첨 테스트 - 성공")
    void pickLotteryEventWinners_Success() {
        //given
        List<LotteryEvent> lotteryEventList = new ArrayList<>();
        lotteryEventList.add(lotteryEvent);
        List<LotteryParticipants> lotteryParticipantsList = new ArrayList<>();

        for (int i = 0; i < 400; i++) {
            BaseUser user = new BaseUser(String.format("010-0000-%04d", i), Role.USER);
            LotteryParticipants lotteryParticipants = new LotteryParticipants(user);
            lotteryParticipantsList.add(lotteryParticipants);
        }

        given(lotteryEventRepository.findAll()).willReturn(lotteryEventList);
        given(lotteryWinnerRepository.count()).willReturn(0L);
        given(lotteryParticipantsRepository.findAll()).willReturn(lotteryParticipantsList);

        //when
        ResponseDto responseDto = adminService.pickLotteryEventWinners();

        //then
        assertThat(responseDto.message()).isEqualTo("추첨이 완료되었습니다.");
    }

    @Test
    @DisplayName("추첨 이벤트 당첨자 추첨 테스트 - 성공 (당첨인원보다 신청인원이 적을 경우)")
    void pickLotteryEventWinners_Success_ParticipantsIsLessThanWinners() {
        //given
        List<LotteryEvent> lotteryEventList = new ArrayList<>();
        lotteryEventList.add(lotteryEvent);
        List<LotteryParticipants> lotteryParticipantsList = new ArrayList<>();
        lotteryParticipantsList.add(lotteryParticipants);


        given(lotteryEventRepository.findAll()).willReturn(lotteryEventList);
        given(lotteryWinnerRepository.count()).willReturn(0L);
        given(lotteryParticipantsRepository.findAll()).willReturn(lotteryParticipantsList);

        //when
        ResponseDto responseDto = adminService.pickLotteryEventWinners();

        //then
        assertThat(responseDto.message()).isEqualTo("추첨이 완료되었습니다.");
    }

    @Test
    @DisplayName("추첨 이벤트 당첨자 추첨 테스트 - 실패 (이미 추첨이 완료된 경우)")
    void pickLotteryEventWinnersTest_Failure_AlreadyDrown() {
        //given
        given(lotteryWinnerRepository.count()).willReturn(315L);

        //when
        CustomException customException = assertThrows(CustomException.class, () ->
                adminService.pickLotteryEventWinners()
        );

        //then
        assertEquals(CustomErrorCode.LOTTERY_EVENT_ALREADY_DRAWN, customException.getErrorCode());
        assertEquals("추첨 이벤트의 당첨자가 이미 추첨되었습니다.", customException.getMessage());
    }

    @Test
    @DisplayName("당첨자 명단 삭제 테스트 - 성공")
    void deleteLotteryEventWinnersTest_Success() {
        //given

        //when
        ResponseDto responseDto = adminService.deleteLotteryEventWinners();

        //then
        assertThat(responseDto.message()).isEqualTo("당첨자 명단을 삭제했습니다.");
    }

    @Test
    @DisplayName("당첨자 명단 조회 테스트 - 성공 (전화번호가 주어지지 않은 경우)")
    void getLotteryEventWinnersTest_Success_WithoutPhoneNumber() {
        //given
        List<LotteryWinners> lotteryWinnersList = new ArrayList<>();
        lotteryWinnersList.add(new LotteryWinners(lotteryParticipants));
        Page<LotteryWinners> lotteryWinnersPage = new PageImpl<>(lotteryWinnersList);

        given(lotteryWinnerRepository.count()).willReturn(315L);
        given(lotteryWinnerRepository.findAll(any(Pageable.class)))
                .willReturn(lotteryWinnersPage);

        //when
        LotteryEventWinnerListResponseDto lotteryEventWinners = adminService.getLotteryEventWinners(1, 0, "");

        //then
        LotteryEventWinnerResponseDto actualWinner = lotteryEventWinners.participantsList().get(0);
        assertThat(actualWinner.phoneNumber()).isEqualTo("010-0000-0000");
        assertThat(actualWinner.linkClickedCounts()).isEqualTo(0);
        assertThat(actualWinner.expectation()).isEqualTo(0);
        assertThat(actualWinner.appliedCount()).isEqualTo(1);
        assertThat(actualWinner.ranking()).isEqualTo(0);
        assertThat(actualWinner.createdDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(actualWinner.createdTime()).isEqualTo(LocalTime.of(0, 0));

        assertThat(lotteryEventWinners.isLastPage()).isTrue();

        assertThat(lotteryEventWinners.totalParticipants()).isEqualTo(315);
    }

    @Test
    @DisplayName("당첨자 명단 조회 테스트 - 성공 (전화번호가 주어진 경우)")
    void getLotteryEventWinnersTest_Success_WithPhoneNumber() {
        //given
        List<LotteryWinners> lotteryWinnersList = new ArrayList<>();
        lotteryWinnersList.add(new LotteryWinners(lotteryParticipants));
        Page<LotteryWinners> lotteryWinnersPage = new PageImpl<>(lotteryWinnersList);

        given(lotteryWinnerRepository.count()).willReturn(315L);
        given(lotteryWinnerRepository.findByPhoneNumber(eq("010-0000-0000"), any(Pageable.class)))
                .willReturn(lotteryWinnersPage);
        given(lotteryWinnerRepository.countByPhoneNumber("010-0000-0000")).willReturn(1L);

        //when
        LotteryEventWinnerListResponseDto lotteryEventWinners = adminService.getLotteryEventWinners(1, 0, "010-0000-0000");

        //then
        LotteryEventWinnerResponseDto actualWinner = lotteryEventWinners.participantsList().get(0);
        assertThat(actualWinner.phoneNumber()).isEqualTo("010-0000-0000");
        assertThat(actualWinner.linkClickedCounts()).isEqualTo(0);
        assertThat(actualWinner.expectation()).isEqualTo(0);
        assertThat(actualWinner.appliedCount()).isEqualTo(1);
        assertThat(actualWinner.ranking()).isEqualTo(0);
        assertThat(actualWinner.createdDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(actualWinner.createdTime()).isEqualTo(LocalTime.of(0, 0));

        assertThat(lotteryEventWinners.isLastPage()).isTrue();

        assertThat(lotteryEventWinners.totalParticipants()).isEqualTo(1);
    }

    @Test
    @DisplayName("추첨 이벤트 당첨자 조회 테스트 - 실패 (아직 추첨하지 않음)")
    void getLotteryEventWinnersTest_Failure_NotDrawn() {
        //given
        given(lotteryWinnerRepository.count()).willReturn(0L);

        //when
        CustomException customException = assertThrows(CustomException.class, () ->
                adminService.getLotteryEventWinners(1, 0, "")
        );

        //then
        assertEquals(CustomErrorCode.LOTTERY_EVENT_NOT_DRAWN, customException.getErrorCode());
        assertEquals("추첨 이벤트가 아직 추첨되지 않았습니다.", customException.getMessage());
    }

    @Test
    @DisplayName("선착순 이벤트 업데이트 테스트 - 성공")
    void updateRushEventTest_Success() {
        //given
        rushEvent.addOption(leftOption, rightOption);
        List<RushEventRequestDto> rushEventRequestDtoList = new ArrayList<>();
        rushEventRequestDtoList.add(rushEventRequestDto);

        List<RushEvent> rushEventList = new ArrayList<>();
        rushEventList.add(rushEvent);

        given(rushEventRepository.findByRushEventId(1L)).willReturn(rushEvent);
        given(rushEventRepository.findAll()).willReturn(rushEventList);

        //when
        List<AdminRushEventResponseDto> rushEventResponseDtoList = adminService.updateRushEvents(rushEventRequestDtoList);

        //then

        AdminRushEventResponseDto actualRushEvent = rushEventResponseDtoList.iterator().next();
        assertThat(actualRushEvent.eventDate()).isEqualTo(LocalDate.of(2024, 8, 15));
        assertThat(actualRushEvent.startTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(actualRushEvent.endTime()).isEqualTo(LocalTime.of(23, 59));
        assertThat(actualRushEvent.winnerCount()).isEqualTo(100);
        assertThat(actualRushEvent.prizeImageUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(actualRushEvent.prizeDescription()).isEqualTo("This is a detailed description of the prize.");
        assertThat(actualRushEvent.status()).isEqualTo(EventStatus.AFTER);

        Set<RushEventOptionResponseDto> options = actualRushEvent.options();

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
    @DisplayName("선착순 이벤트 업데이트 테스트 - 실패 (종료 시간이 시작 시간보다 앞서는 경우)")
    void updateRushEventTest_Failure_EndBeforeStart() {
        //given
        rushEvent.addOption(leftOption, rightOption);
        List<RushEventRequestDto> rushEventRequestDtoList = new ArrayList<>();

        Set<RushEventOptionRequestDto> options = new HashSet<>();
        options.add(leftOptionRequestDto);
        options.add(rightOptionRequestDto);

        rushEventRequestDto = RushEventRequestDto.builder()
                .rushEventId(1L)
                .eventDate(LocalDate.of(2024, 8, 15))
                .startTime(LocalTime.of(23, 59))
                .endTime(LocalTime.of(0, 0))
                .winnerCount(100)
                .prizeImageUrl("http://example.com/image.jpg")
                .prizeDescription("This is a detailed description of the prize.")
                .options(options)
                .build();

        rushEventRequestDtoList.add(rushEventRequestDto);

        List<RushEvent> rushEventList = new ArrayList<>();
        rushEventList.add(rushEvent);

        given(rushEventRepository.findByRushEventId(1L)).willReturn(rushEvent);

        //when
        CustomException customException = assertThrows(CustomException.class, () ->
                adminService.updateRushEvents(rushEventRequestDtoList)
        );

        //then
        assertEquals(CustomErrorCode.EVENT_END_TIME_BEFORE_START_TIME, customException.getErrorCode());
        assertEquals("종료 시간은 시작 시간 이후로 설정해야 합니다.", customException.getMessage());
    }

    @Test
    @DisplayName("선착순 이벤트 업데이트 테스트 - 실패 (진행중인 이벤트의 시작 시간을 수정하는 경우)")
    void updateRushEventTest_Failure_ModifyingOngoingEvent() {
        //given
        Set<RushEventOptionRequestDto> options = new HashSet<>();
        options.add(leftOptionRequestDto);
        options.add(rightOptionRequestDto);

        rushEventRequestDto = RushEventRequestDto.builder()
                .rushEventId(1L)
                .eventDate(LocalDate.now())
                .startTime(LocalTime.of(0, 0))
                .endTime(LocalTime.of(23, 59))
                .winnerCount(100)
                .prizeImageUrl("http://example.com/image.jpg")
                .prizeDescription("This is a detailed description of the prize.")
                .options(options)
                .build();

        rushEvent = new RushEvent(
                LocalDateTime.of(rushEventRequestDto.getEventDate(), rushEventRequestDto.getStartTime()),
                LocalDateTime.of(rushEventRequestDto.getEventDate(), rushEventRequestDto.getEndTime()),
                rushEventRequestDto.getWinnerCount(),
                "http://example.com/image.jpg",
                rushEventRequestDto.getPrizeDescription()
        );

        rushEvent.addOption(leftOption, rightOption);
        List<RushEventRequestDto> rushEventRequestDtoList = new ArrayList<>();

        rushEventRequestDto = RushEventRequestDto.builder()
                .rushEventId(1L)
                .eventDate(LocalDate.now())
                .startTime(LocalTime.of(23, 58))
                .endTime(LocalTime.of(23, 59))
                .winnerCount(100)
                .prizeImageUrl("http://example.com/image.jpg")
                .prizeDescription("This is a detailed description of the prize.")
                .options(options)
                .build();

        rushEventRequestDtoList.add(rushEventRequestDto);

        List<RushEvent> rushEventList = new ArrayList<>();
        rushEventList.add(rushEvent);

        given(rushEventRepository.findByRushEventId(1L)).willReturn(rushEvent);

        //when
        CustomException customException = assertThrows(CustomException.class, () ->
                adminService.updateRushEvents(rushEventRequestDtoList)
        );

        //then
        assertEquals(CustomErrorCode.EVENT_IN_PROGRESS_CANNOT_CHANGE_START_TIME, customException.getErrorCode());
        assertEquals("현재 진행 중인 이벤트의 시작 시간을 변경할 수 없습니다.", customException.getMessage());
    }

    @Test
    @DisplayName("선착순 이벤트 업데이트 테스트 - 실패 (진행중인 이벤트의 종료 시간을 수정하는 경우)")
    void updateRushEventTest_Failure_EndBeforeNow() {
        //given
        Set<RushEventOptionRequestDto> options = new HashSet<>();
        options.add(leftOptionRequestDto);
        options.add(rightOptionRequestDto);

        rushEventRequestDto = RushEventRequestDto.builder()
                .rushEventId(1L)
                .eventDate(LocalDate.now())
                .startTime(LocalTime.of(0, 0))
                .endTime(LocalTime.of(23, 59))
                .winnerCount(100)
                .prizeImageUrl("http://example.com/image.jpg")
                .prizeDescription("This is a detailed description of the prize.")
                .options(options)
                .build();

        rushEvent = new RushEvent(
                LocalDateTime.of(rushEventRequestDto.getEventDate(), rushEventRequestDto.getStartTime()),
                LocalDateTime.of(rushEventRequestDto.getEventDate(), rushEventRequestDto.getEndTime()),
                rushEventRequestDto.getWinnerCount(),
                "http://example.com/image.jpg",
                rushEventRequestDto.getPrizeDescription()
        );

        rushEvent.addOption(leftOption, rightOption);
        List<RushEventRequestDto> rushEventRequestDtoList = new ArrayList<>();

        rushEventRequestDto = RushEventRequestDto.builder()
                .rushEventId(1L)
                .eventDate(LocalDate.now())
                .startTime(LocalTime.of(0, 0))
                .endTime(LocalTime.now().minusSeconds(5))
                .winnerCount(100)
                .prizeImageUrl("http://example.com/image.jpg")
                .prizeDescription("This is a detailed description of the prize.")
                .options(options)
                .build();

        rushEventRequestDtoList.add(rushEventRequestDto);

        List<RushEvent> rushEventList = new ArrayList<>();
        rushEventList.add(rushEvent);

        given(rushEventRepository.findByRushEventId(1L)).willReturn(rushEvent);

        //when
        CustomException customException = assertThrows(CustomException.class, () ->
                adminService.updateRushEvents(rushEventRequestDtoList)
        );

        //then
        assertEquals(CustomErrorCode.EVENT_IN_PROGRESS_END_TIME_BEFORE_NOW, customException.getErrorCode());
        assertEquals("현재 진행 중인 이벤트의 종료 시간을 현재 시간보다 이전으로 설정할 수 없습니다.", customException.getMessage());
    }

    @Test
    @DisplayName("선착순 이벤트 업데이트 테스트 - 실패 (이벤트 시작 전인 경우)")
    void updateRushEventTest_Failure_StartBeforeNow() {
        //given
        Set<RushEventOptionRequestDto> options = new HashSet<>();
        options.add(leftOptionRequestDto);
        options.add(rightOptionRequestDto);

        rushEventRequestDto = RushEventRequestDto.builder()
                .rushEventId(1L)
                .eventDate(LocalDate.of(2100, 1, 1))
                .startTime(LocalTime.of(0, 0))
                .endTime(LocalTime.of(23, 59))
                .winnerCount(100)
                .prizeImageUrl("http://example.com/image.jpg")
                .prizeDescription("This is a detailed description of the prize.")
                .options(options)
                .build();

        rushEvent = new RushEvent(
                LocalDateTime.of(rushEventRequestDto.getEventDate(), rushEventRequestDto.getStartTime()),
                LocalDateTime.of(rushEventRequestDto.getEventDate(), rushEventRequestDto.getEndTime()),
                rushEventRequestDto.getWinnerCount(),
                "http://example.com/image.jpg",
                rushEventRequestDto.getPrizeDescription()
        );

        rushEvent.addOption(leftOption, rightOption);
        List<RushEventRequestDto> rushEventRequestDtoList = new ArrayList<>();

        rushEventRequestDto = RushEventRequestDto.builder()
                .rushEventId(1L)
                .eventDate(LocalDate.of(1945, 8, 15))
                .startTime(LocalTime.of(0, 0))
                .endTime(LocalTime.now().minusSeconds(5))
                .winnerCount(100)
                .prizeImageUrl("http://example.com/image.jpg")
                .prizeDescription("This is a detailed description of the prize.")
                .options(options)
                .build();

        rushEventRequestDtoList.add(rushEventRequestDto);

        List<RushEvent> rushEventList = new ArrayList<>();
        rushEventList.add(rushEvent);

        given(rushEventRepository.findByRushEventId(1L)).willReturn(rushEvent);

        //when
        CustomException customException = assertThrows(CustomException.class, () ->
                adminService.updateRushEvents(rushEventRequestDtoList)
        );

        //then
        assertEquals(CustomErrorCode.EVENT_BEFORE_START_TIME, customException.getErrorCode());
        assertEquals("이벤트 시작 시간은 현재 시간 이후로 설정해야 합니다.", customException.getMessage());
    }

    @Test
    @DisplayName("선착순 이벤트 삭제 테스트 - 성공")
    void deleteRushEventTest_Success() {
        //given
        given(rushEventRepository.findById(1L)).willReturn(Optional.ofNullable(rushEvent));

        //when
        ResponseDto responseDto = adminService.deleteRushEvent(1L);

        //then
        assertThat(responseDto.message()).isEqualTo("요청에 성공하였습니다.");
    }

    @Test
    @DisplayName("선착순 이벤트 삭제 테스트 - 실패 (아이디와 일치하는 이벤트가 없는 경우)")
    void deleteRushEventTest_Failure_NoLotteryEvent() {
        //given
        given(rushEventRepository.findById(1L)).willReturn(Optional.empty());

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> adminService.deleteRushEvent(1L)
        );

        //then
        assertEquals(CustomErrorCode.NO_RUSH_EVENT, customException.getErrorCode());
        assertEquals("선착순 이벤트를 찾을 수 없습니다.", customException.getMessage());
    }
}
