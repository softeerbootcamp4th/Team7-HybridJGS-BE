package JGS.CasperEvent.domain.event.service.adminService;

import JGS.CasperEvent.domain.event.dto.request.AdminRequestDto;
import JGS.CasperEvent.domain.event.dto.request.lotteryEventDto.CasperBotRequestDto;
import JGS.CasperEvent.domain.event.dto.request.lotteryEventDto.LotteryEventRequestDto;
import JGS.CasperEvent.domain.event.dto.request.rushEventDto.RushEventOptionRequestDto;
import JGS.CasperEvent.domain.event.dto.request.rushEventDto.RushEventRequestDto;
import JGS.CasperEvent.domain.event.dto.response.ImageUrlResponseDto;
import JGS.CasperEvent.domain.event.dto.response.ParticipantsListResponseDto;
import JGS.CasperEvent.domain.event.dto.response.lottery.CasperBotResponseDto;
import JGS.CasperEvent.domain.event.dto.response.lottery.ExpectationsPagingResponseDto;
import JGS.CasperEvent.domain.event.dto.response.lottery.LotteryEventParticipantResponseDto;
import JGS.CasperEvent.domain.event.dto.response.lottery.LotteryEventResponseDto;
import JGS.CasperEvent.domain.event.dto.response.rush.RushEventOptionResponseDto;
import JGS.CasperEvent.domain.event.dto.response.rush.RushEventParticipantResponseDto;
import JGS.CasperEvent.domain.event.dto.response.rush.RushEventResponseDto;
import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
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
import JGS.CasperEvent.domain.event.service.eventService.EventCacheService;
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
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
    @Mock
    private RedisTemplate<String, CasperBotResponseDto> casperBotRedisTemplate;
    @Mock
    private ListOperations<String, CasperBotResponseDto> listOperations;
    @Mock
    private EventCacheService eventCacheService;


    private RushEvent rushEvent;
    private RushOption leftOption;
    private RushOption rightOption;

    private Admin admin;
    private BaseUser user;
    private LotteryEvent lotteryEvent;
    private LotteryEventRequestDto lotteryEventRequestDto;
    private LotteryParticipants lotteryParticipants;
    private CasperBotRequestDto casperBotRequestDto;
    private CasperBot casperBot;

    private RushEventRequestDto rushEventRequestDto;
    private RushEventOptionRequestDto leftOptionRequestDto;
    private RushEventOptionRequestDto rightOptionRequestDto;
    private RushParticipants rushParticipant;

    @InjectMocks
    AdminService adminService;

    @BeforeEach
    void setUp() {
        // 어드민 객체
        admin = new Admin("adminId", "password", Role.ADMIN);

        // 유저 객체
        user = spy(new BaseUser("010-0000-0000", Role.USER));
        lenient().when(user.getCreatedAt()).thenReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        lenient().when(user.getUpdatedAt()).thenReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0));

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
        lotteryParticipants = spy(new LotteryParticipants(user));
        lenient().when(lotteryParticipants.getId()).thenReturn(1L);
        lenient().when(lotteryParticipants.getCreatedAt()).thenReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        lenient().when(lotteryParticipants.getUpdatedAt()).thenReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0));

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
        rushParticipant = spy(new RushParticipants(user, rushEvent, 1));
        lenient().when(rushParticipant.getCreatedAt()).thenReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        lenient().when(rushParticipant.getUpdatedAt()).thenReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0));


        // 캐스퍼 봇 생성
        casperBotRequestDto = CasperBotRequestDto.builder()
                        .

                eyeShape(0)
                        .

                eyePosition(0)
                        .

                mouthShape(0)
                        .

                color(0)
                        .

                sticker(0)
                        .

                name("name")
                        .

                expectation("expectation")
                        .

                referralId("QEszP1K8IqcapUHAVwikXA==").

                build();

        casperBot =
                spy(new CasperBot(casperBotRequestDto, "010-0000-0000"));
        lenient().
                when(casperBot.getCasperId()).
                thenReturn(1L);
        lenient().
                when(casperBot.getCreatedAt()).
                thenReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        lenient().
                when(casperBot.getUpdatedAt()).
                thenReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
    }

    @Test
    @DisplayName("어드민 인증 테스트 - 성공")
    void verifyAdminTest_Success() {
        //given
        AdminRequestDto adminRequestDto = AdminRequestDto.builder()
                .adminId("adminId")
                .password("password")
                .build();

        given(adminRepository.findByPhoneNumberAndPassword("adminId", "password")).willReturn(Optional.ofNullable(admin));

        //when
        admin = adminService.verifyAdmin(adminRequestDto);

        //then
        assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
        assertThat(admin.getPhoneNumber()).isEqualTo("adminId");
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

        given(adminRepository.findByPhoneNumber("adminId")).willReturn(Optional.ofNullable(admin));

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
        assertThat(lotteryEventResponseDto.getServerDateTime()).isNotNull();
        assertThat(lotteryEventResponseDto.getEventStartDate()).isEqualTo("2000-09-27T00:00");
        assertThat(lotteryEventResponseDto.getEventEndDate()).isEqualTo("2100-09-27T00:00");
        assertThat(lotteryEventResponseDto.getActivePeriod()).isEqualTo(36524);
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

        LotteryEventResponseDto lotteryEventResponseDto = adminService.getLotteryEvent();

        //then
        assertThat(lotteryEventResponseDto.getStartDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(lotteryEventResponseDto.getStartTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(lotteryEventResponseDto.getEndDate()).isEqualTo(LocalDate.of(2100, 9, 27));
        assertThat(lotteryEventResponseDto.getEndTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(lotteryEventResponseDto.getAppliedCount()).isZero();
        assertThat(lotteryEventResponseDto.getWinnerCount()).isEqualTo(315);
        assertThat(lotteryEventResponseDto.getStatus()).isEqualTo(EventStatus.DURING);
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
        ParticipantsListResponseDto<LotteryEventParticipantResponseDto> lotteryEventParticipantsListResponseDto = adminService.getLotteryEventParticipants(10, 0, "");
        LotteryEventParticipantResponseDto retrievedParticipant = lotteryEventParticipantsListResponseDto.participantsList().get(0);

        //then
        assertThat(lotteryEventParticipantsListResponseDto.isLastPage()).isTrue();
        assertThat(lotteryEventParticipantsListResponseDto.totalParticipants()).isEqualTo(1);

        assertThat(retrievedParticipant.getPhoneNumber()).isEqualTo("010-0000-0000");
        assertThat(retrievedParticipant.getLinkClickedCounts()).isZero();
        assertThat(retrievedParticipant.getExpectation()).isZero();
        assertThat(retrievedParticipant.getAppliedCount()).isEqualTo(1);
        assertThat(retrievedParticipant.getCreatedDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(retrievedParticipant.getCreatedTime()).isEqualTo(LocalTime.of(0, 0, 0));
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
        ParticipantsListResponseDto<LotteryEventParticipantResponseDto> lotteryEventParticipantsListResponseDto = adminService.getLotteryEventParticipants(10, 0, "010-0000-0000");
        LotteryEventParticipantResponseDto retrievedParticipant = lotteryEventParticipantsListResponseDto.participantsList().get(0);

        //then
        assertThat(lotteryEventParticipantsListResponseDto.isLastPage()).isTrue();
        assertThat(lotteryEventParticipantsListResponseDto.totalParticipants()).isEqualTo(1);

        assertThat(retrievedParticipant.getPhoneNumber()).isEqualTo("010-0000-0000");
        assertThat(retrievedParticipant.getLinkClickedCounts()).isZero();
        assertThat(retrievedParticipant.getExpectation()).isZero();
        assertThat(retrievedParticipant.getAppliedCount()).isEqualTo(1);
        assertThat(retrievedParticipant.getCreatedDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(retrievedParticipant.getCreatedTime()).isEqualTo(LocalTime.of(0, 0, 0));
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
        RushEventResponseDto adminRushEventResponseDto = adminService.createRushEvent(rushEventRequestDto, prizeImg, leftOptionImg, rightOptionImg);

        //then
        assertThat(adminRushEventResponseDto.getEventDate()).isEqualTo(LocalDate.of(2024, 8, 15));
        assertThat(adminRushEventResponseDto.getStartTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(adminRushEventResponseDto.getEndTime()).isEqualTo(LocalTime.of(23, 59));
        assertThat(adminRushEventResponseDto.getWinnerCount()).isEqualTo(100);
        assertThat(adminRushEventResponseDto.getPrizeImageUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(adminRushEventResponseDto.getPrizeDescription()).isEqualTo("This is a detailed description of the prize.");
        assertThat(adminRushEventResponseDto.getStatus()).isEqualTo(EventStatus.AFTER);

        Set<RushEventOptionResponseDto> options = adminRushEventResponseDto.getOptions();

        boolean firstOptionFound = false;
        boolean secondOptionFound = false;

        for (RushEventOptionResponseDto option : options) {
            if (option.getMainText().equals("Main Text 2") &&
                    option.getSubText().equals("Sub Text 2") &&
                    option.getResultMainText().equals("Result Main Text 2") &&
                    option.getResultSubText().equals("Result Sub Text 2") &&
                    option.getImageUrl().equals("http://example.com/image.jpg") &&
                    option.getPosition().equals(Position.RIGHT)) {
                firstOptionFound = true;
            } else if (option.getMainText().equals("Main Text 1") &&
                    option.getSubText().equals("Sub Text 1") &&
                    option.getResultMainText().equals("Result Main Text 1") &&
                    option.getResultSubText().equals("Result Sub Text 1") &&
                    option.getImageUrl().equals("http://example.com/image.jpg") &&
                    option.getPosition().equals(Position.LEFT)) {
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
        List<RushEventResponseDto> rushEvents = adminService.getRushEvents();

        //then
        RushEventResponseDto firstEvent = rushEvents.get(0);
        assertThat(firstEvent.getEventDate()).isEqualTo(LocalDate.of(2024, 8, 15));
        assertThat(firstEvent.getStartTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(firstEvent.getEndTime()).isEqualTo(LocalTime.of(23, 59));
        assertThat(firstEvent.getWinnerCount()).isEqualTo(100);
        assertThat(firstEvent.getPrizeImageUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(firstEvent.getPrizeDescription()).isEqualTo("This is a detailed description of the prize.");
        assertThat(firstEvent.getStatus()).isEqualTo(EventStatus.AFTER);

        Set<RushEventOptionResponseDto> options = firstEvent.getOptions();


        boolean firstOptionFound = false;
        boolean secondOptionFound = false;
        for (RushEventOptionResponseDto option : options) {
            System.out.println("option = " + option);
            if (option.getMainText().equals("Main Text 2") &&
                    option.getSubText().equals("Sub Text 2") &&
                    option.getResultMainText().equals("Result Main Text 2") &&
                    option.getResultSubText().equals("Result Sub Text 2") &&
                    option.getImageUrl().equals("http://example.com/image.jpg") &&
                    option.getPosition().equals(Position.RIGHT)) {
                firstOptionFound = true;
            } else if (option.getMainText().equals("Main Text 1") &&
                    option.getSubText().equals("Sub Text 1") &&
                    option.getResultMainText().equals("Result Main Text 1") &&
                    option.getResultSubText().equals("Result Sub Text 1") &&
                    option.getImageUrl().equals("http://example.com/image.jpg") &&
                    option.getPosition().equals(Position.LEFT)) {
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
        rushParticipantsList.add(rushParticipant);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushParticipantsRepository.findByRushEvent_RushEventIdAndOptionIdAndBaseUser_Id(eq(1L), eq(1), eq("010-0000-0000"), any(Pageable.class)))
                .willReturn(rushParticipantsPage);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionIdAndBaseUser_Id(1L, 1, "010-0000-0000"))
                .willReturn(1L);

        //when
        ParticipantsListResponseDto<RushEventParticipantResponseDto> rushEventParticipants = adminService.getRushEventParticipants(1, 1, 0, 1, "010-0000-0000");

        //then
        assertThat(rushEventParticipants.isLastPage()).isTrue();
        assertThat(rushEventParticipants.totalParticipants()).isEqualTo(1);

        List<RushEventParticipantResponseDto> participantsList = rushEventParticipants.participantsList();

        RushEventParticipantResponseDto participant = participantsList.get(0);

        assertThat(participant.getPhoneNumber()).isEqualTo("010-0000-0000");
        assertThat(participant.getBalanceGameChoice()).isEqualTo(1);
        assertThat(participant.getCreatedDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(participant.getCreatedTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(participant.getRank()).isZero();

    }

    @Test
    @DisplayName("선착순 이벤트 참여자 조회 테스트 - 성공 (전화번호가 존재하지 않고 결과가 동점인 경우")
    void getRushEventParticipantsTest_Success_withoutPhoneNumberAndOptionId() {
        //given
        List<RushParticipants> rushParticipantsList = new ArrayList<>();
        rushParticipantsList.add(rushParticipant);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushParticipantsRepository.findByRushEvent_RushEventId(eq(1L), any(Pageable.class)))
                .willReturn(rushParticipantsPage);
        given(rushParticipantsRepository.countByRushEvent_RushEventId(1L))
                .willReturn(1L);

        //when
        ParticipantsListResponseDto<RushEventParticipantResponseDto> rushEventParticipants = adminService.getRushEventParticipants(1, 1, 0, 0, "");

        //then
        assertThat(rushEventParticipants.isLastPage()).isTrue();
        assertThat(rushEventParticipants.totalParticipants()).isEqualTo(1);

        List<RushEventParticipantResponseDto> participantsList = rushEventParticipants.participantsList();

        RushEventParticipantResponseDto participant = participantsList.get(0);

        assertThat(participant.getPhoneNumber()).isEqualTo("010-0000-0000");
        assertThat(participant.getBalanceGameChoice()).isEqualTo(1);
        assertThat(participant.getCreatedDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(participant.getCreatedTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(participant.getRank()).isZero();
    }

    @Test
    @DisplayName("선착순 이벤트 참여자 조회 테스트 - 성공 (전화번호가 존재하지 않고 결과가 동점이 아닌 경우")
    void getRushEventParticipantsTest_Success_withoutPhoneNumberWithOptionId() {
        //given
        List<RushParticipants> rushParticipantsList = new ArrayList<>();
        rushParticipantsList.add(rushParticipant);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushParticipantsRepository.findByRushEvent_RushEventIdAndOptionId(eq(1L), eq(1), any(Pageable.class)))
                .willReturn(rushParticipantsPage);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1))
                .willReturn(1L);

        //when
        ParticipantsListResponseDto<RushEventParticipantResponseDto> rushEventParticipants = adminService.getRushEventParticipants(1, 1, 0, 1, "");

        //then
        assertThat(rushEventParticipants.isLastPage()).isTrue();
        assertThat(rushEventParticipants.totalParticipants()).isEqualTo(1);

        List<RushEventParticipantResponseDto> participantsList = rushEventParticipants.participantsList();

        RushEventParticipantResponseDto participant = participantsList.get(0);

        assertThat(participant.getPhoneNumber()).isEqualTo("010-0000-0000");
        assertThat(participant.getBalanceGameChoice()).isEqualTo(1);
        assertThat(participant.getCreatedDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(participant.getCreatedTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(participant.getRank()).isZero();
    }

    @Test
    @DisplayName("선착순 이벤트 참여자 조회 테스트 - 성공 (전화번호가 존재하고 결과가 동점인 경우")
    void getRushEventParticipantsTest_Success_witPhoneNumberAndWithoutOptionId() {
        //given
        List<RushParticipants> rushParticipantsList = new ArrayList<>();
        rushParticipantsList.add(rushParticipant);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushParticipantsRepository.findByRushEvent_RushEventIdAndBaseUser_Id(eq(1L), eq("010-0000-0000"), any(Pageable.class)))
                .willReturn(rushParticipantsPage);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndBaseUser_Id(1L, "010-0000-0000"))
                .willReturn(1L);

        //when
        ParticipantsListResponseDto<RushEventParticipantResponseDto> rushEventParticipants = adminService.getRushEventParticipants(1, 1, 0, 0, "010-0000-0000");

        //then
        assertThat(rushEventParticipants.isLastPage()).isTrue();
        assertThat(rushEventParticipants.totalParticipants()).isEqualTo(1);

        List<RushEventParticipantResponseDto> participantsList = rushEventParticipants.participantsList();

        RushEventParticipantResponseDto participant = participantsList.get(0);

        assertThat(participant.getPhoneNumber()).isEqualTo("010-0000-0000");
        assertThat(participant.getBalanceGameChoice()).isEqualTo(1);
        assertThat(participant.getCreatedDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(participant.getCreatedTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(participant.getRank()).isZero();
    }

    @Test
    @DisplayName("선착순 이벤트 당첨자 조회 테스트 - 성공 (전화번호가 존재하고 결과가 동점이 아닌 경우")
    void getRushEventWinnersTest_Success_withPhoneNumberAndOptionId() {
        //given
        List<RushParticipants> rushParticipantsList = new ArrayList<>();
        rushParticipantsList.add(rushParticipant);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushEventRepository.findById(1L)).willReturn(Optional.of(rushEvent));
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1))
                .willReturn(2L);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 2))
                .willReturn(1L);
        given(rushParticipantsRepository.findWinnerByEventIdAndOptionIdAndPhoneNumber(eq(1L), eq(1), eq("010-0000-0000"), any(Pageable.class)))
                .willReturn(rushParticipantsPage);

        //when
        ParticipantsListResponseDto<RushEventParticipantResponseDto> rushEventWinners
                = adminService.getRushEventWinners(1L, 1, 0, "010-0000-0000");

        //then
        assertThat(rushEventWinners.isLastPage()).isTrue();
        assertThat(rushEventWinners.totalParticipants()).isEqualTo(1);

        List<RushEventParticipantResponseDto> participantsList = rushEventWinners.participantsList();

        RushEventParticipantResponseDto participant = participantsList.get(0);

        assertThat(participant.getPhoneNumber()).isEqualTo("010-0000-0000");
        assertThat(participant.getBalanceGameChoice()).isEqualTo(1);
        assertThat(participant.getCreatedDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(participant.getCreatedTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(participant.getRank()).isZero();
    }

    @Test
    @DisplayName("선착순 이벤트 당첨자 조회 테스트 - 성공 (전화번호가 존재하지 않고 결과가 동점인 경우")
    void getRushEventWinnersTest_Success_withoutPhoneNumberAndOptionId() {
        //given
        List<RushParticipants> rushParticipantsList = new ArrayList<>();
        rushParticipantsList.add(rushParticipant);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushEventRepository.findById(1L)).willReturn(Optional.of(rushEvent));
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1))
                .willReturn(1L);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 2))
                .willReturn(1L);
        given(rushParticipantsRepository.findWinnerByEventId(eq(1L), any(Pageable.class)))
                .willReturn(rushParticipantsPage);

        //when
        ParticipantsListResponseDto<RushEventParticipantResponseDto> rushEventWinners
                = adminService.getRushEventWinners(1L, 1, 0, "");

        //then
        assertThat(rushEventWinners.isLastPage()).isTrue();
        assertThat(rushEventWinners.totalParticipants()).isEqualTo(1);

        List<RushEventParticipantResponseDto> participantsList = rushEventWinners.participantsList();

        RushEventParticipantResponseDto participant = participantsList.get(0);

        assertThat(participant.getPhoneNumber()).isEqualTo("010-0000-0000");
        assertThat(participant.getBalanceGameChoice()).isEqualTo(1);
        assertThat(participant.getCreatedDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(participant.getCreatedTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(participant.getRank()).isZero();
    }

    @Test
    @DisplayName("선착순 이벤트 당첨자 조회 테스트 - 성공 (전화번호가 존재하지 않고 결과가 동점이 아닌 경우")
    void getRushEventWinnersTest_Success_withoutPhoneNumberAndWithOptionId() {
        //given
        List<RushParticipants> rushParticipantsList = new ArrayList<>();
        rushParticipantsList.add(rushParticipant);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushEventRepository.findById(1L)).willReturn(Optional.of(rushEvent));
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1))
                .willReturn(2L);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 2))
                .willReturn(1L);
        given(rushParticipantsRepository.findWinnerByEventIdAndOptionId(eq(1L), eq(1), any(Pageable.class)))
                .willReturn(rushParticipantsPage);

        //when
        ParticipantsListResponseDto<RushEventParticipantResponseDto> rushEventWinners
                = adminService.getRushEventWinners(1L, 1, 0, "");

        //then
        assertThat(rushEventWinners.isLastPage()).isTrue();
        assertThat(rushEventWinners.totalParticipants()).isEqualTo(1);

        List<RushEventParticipantResponseDto> participantsList = rushEventWinners.participantsList();

        RushEventParticipantResponseDto participant = participantsList.get(0);

        assertThat(participant.getPhoneNumber()).isEqualTo("010-0000-0000");
        assertThat(participant.getBalanceGameChoice()).isEqualTo(1);
        assertThat(participant.getCreatedDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(participant.getCreatedTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(participant.getRank()).isZero();
    }

    @Test
    @DisplayName("선착순 이벤트 당첨자 조회 테스트 - 성공 (전화번호가 존재하고 결과가 동점인 경우")
    void getRushEventWinnersTest_Success_withPhoneNumberAndWithoutOptionId() {
        //given
        List<RushParticipants> rushParticipantsList = new ArrayList<>();
        rushParticipantsList.add(rushParticipant);
        Page<RushParticipants> rushParticipantsPage = new PageImpl<>(rushParticipantsList);

        given(rushEventRepository.findById(1L)).willReturn(Optional.of(rushEvent));
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1))
                .willReturn(1L);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 2))
                .willReturn(1L);
        given(rushParticipantsRepository.findByWinnerByEventIdAndPhoneNumber(eq(1L), eq("010-0000-0000"), any(Pageable.class)))
                .willReturn(rushParticipantsPage);

        //when
        ParticipantsListResponseDto<RushEventParticipantResponseDto> rushEventWinners
                = adminService.getRushEventWinners(1L, 1, 0, "010-0000-0000");

        //then
        assertThat(rushEventWinners.isLastPage()).isTrue();
        assertThat(rushEventWinners.totalParticipants()).isEqualTo(1);

        List<RushEventParticipantResponseDto> participantsList = rushEventWinners.participantsList();

        RushEventParticipantResponseDto participant = participantsList.get(0);

        assertThat(participant.getPhoneNumber()).isEqualTo("010-0000-0000");
        assertThat(participant.getBalanceGameChoice()).isEqualTo(1);
        assertThat(participant.getCreatedDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(participant.getCreatedTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(participant.getRank()).isZero();
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
        given(eventCacheService.setLotteryEvent()).willReturn(lotteryEvent);

        //when
        LotteryEventResponseDto lotteryEventResponseDto = adminService.updateLotteryEvent(lotteryEventRequestDto);

        //then
        assertThat(lotteryEventResponseDto.getStartDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(lotteryEventResponseDto.getStartTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(lotteryEventResponseDto.getEndDate()).isEqualTo(LocalDate.of(2100, 9, 27));
        assertThat(lotteryEventResponseDto.getEndTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(lotteryEventResponseDto.getAppliedCount()).isZero();
        assertThat(lotteryEventResponseDto.getWinnerCount()).isEqualTo(315);
        assertThat(lotteryEventResponseDto.getStatus()).isEqualTo(EventStatus.DURING);
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
            lotteryParticipantsList.add(lotteryParticipants);
        }

        given(lotteryEventRepository.findAll()).willReturn(lotteryEventList);
        given(lotteryWinnerRepository.count()).willReturn(0L);

        List<Object[]> idAndAppliedCounts = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            Object[] data = new Object[2];
            data[0] = (long) i;
            data[1] = 2;
            idAndAppliedCounts.add(data);
        }
        given(lotteryParticipantsRepository.findById(any())).willReturn(Optional.ofNullable(lotteryParticipants));
        given(lotteryParticipantsRepository.findIdAndAppliedCounts()).willReturn(idAndAppliedCounts);

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
        List<Object[]> idAndAppliedCounts = new ArrayList<>();
        Object[] data = new Object[2];
        data[0] = 1L;
        data[1] = 2;
        idAndAppliedCounts.add(data);
        given(lotteryParticipantsRepository.findById(1L)).willReturn(Optional.ofNullable(lotteryParticipants));
        given(lotteryParticipantsRepository.findIdAndAppliedCounts()).willReturn(idAndAppliedCounts);

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
        ParticipantsListResponseDto<LotteryEventParticipantResponseDto> lotteryEventWinners = adminService.getLotteryEventWinners(1, 0, "");

        //then
        LotteryEventParticipantResponseDto actualWinner = lotteryEventWinners.participantsList().get(0);
        assertThat(actualWinner.getPhoneNumber()).isEqualTo("010-0000-0000");
        assertThat(actualWinner.getLinkClickedCounts()).isZero();
        assertThat(actualWinner.getExpectation()).isZero();
        assertThat(actualWinner.getAppliedCount()).isEqualTo(1);
        assertThat(actualWinner.getRanking()).isZero();
        assertThat(actualWinner.getCreatedDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(actualWinner.getCreatedTime()).isEqualTo(LocalTime.of(0, 0));

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
        ParticipantsListResponseDto<LotteryEventParticipantResponseDto> lotteryEventWinners = adminService.getLotteryEventWinners(1, 0, "010-0000-0000");

        //then
        LotteryEventParticipantResponseDto actualWinner = lotteryEventWinners.participantsList().get(0);
        assertThat(actualWinner.getPhoneNumber()).isEqualTo("010-0000-0000");
        assertThat(actualWinner.getLinkClickedCounts()).isZero();
        assertThat(actualWinner.getExpectation()).isZero();
        assertThat(actualWinner.getAppliedCount()).isEqualTo(1);
        assertThat(actualWinner.getRanking()).isZero();
        assertThat(actualWinner.getCreatedDate()).isEqualTo(LocalDate.of(2000, 9, 27));
        assertThat(actualWinner.getCreatedTime()).isEqualTo(LocalTime.of(0, 0));

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
        List<RushEventResponseDto> rushEventResponseDtoList = adminService.updateRushEvents(rushEventRequestDtoList);

        //then

        RushEventResponseDto actualRushEvent = rushEventResponseDtoList.iterator().next();
        assertThat(actualRushEvent.getEventDate()).isEqualTo(LocalDate.of(2024, 8, 15));
        assertThat(actualRushEvent.getStartTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(actualRushEvent.getEndTime()).isEqualTo(LocalTime.of(23, 59));
        assertThat(actualRushEvent.getWinnerCount()).isEqualTo(100);
        assertThat(actualRushEvent.getPrizeImageUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(actualRushEvent.getPrizeDescription()).isEqualTo("This is a detailed description of the prize.");
        assertThat(actualRushEvent.getStatus()).isEqualTo(EventStatus.AFTER);

        Set<RushEventOptionResponseDto> options = actualRushEvent.getOptions();


        boolean firstOptionFound = false;
        boolean secondOptionFound = false;

        for (RushEventOptionResponseDto option : options) {
            if (option.getMainText().equals("Main Text 2") &&
                    option.getSubText().equals("Sub Text 2") &&
                    option.getResultMainText().equals("Result Main Text 2") &&
                    option.getResultSubText().equals("Result Sub Text 2") &&
                    option.getImageUrl().equals("http://example.com/image.jpg") &&
                    option.getPosition().equals(Position.RIGHT)) {
                firstOptionFound = true;
            } else if (option.getMainText().equals("Main Text 1") &&
                    option.getSubText().equals("Sub Text 1") &&
                    option.getResultMainText().equals("Result Main Text 1") &&
                    option.getResultSubText().equals("Result Sub Text 1") &&
                    option.getImageUrl().equals("http://example.com/image.jpg") &&
                    option.getPosition().equals(Position.LEFT)) {
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

    @Test
    @DisplayName("선착순 이벤트 삭제 테스트 - 실패 (진행중인 이벤트일 경우)")
    void deleteRushEventTest_Failure_EventInProgress() {
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

        given(rushEventRepository.findById(1L)).willReturn(Optional.ofNullable(rushEvent));

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> adminService.deleteRushEvent(1L)
        );

        //then
        assertEquals(CustomErrorCode.EVENT_IN_PROGRESS_CANNOT_DELETE, customException.getErrorCode());
        assertEquals("진행중인 이벤트를 삭제할 수 없습니다.", customException.getMessage());
    }

    @Test
    @DisplayName("선착순 이벤트 선택지 조회 테스트 - 성공")
    void getRushEventOptionsTest_Success() {
        //given
        rushEvent.addOption(leftOption, rightOption);
        given(rushEventRepository.findById(1L)).willReturn(Optional.ofNullable(rushEvent));

        //when
        RushEventResponseDto rushEventOptions = adminService.getRushEventOptions(1L);

        //then
        Set<RushEventOptionResponseDto> options = rushEventOptions.getOptions();

        boolean firstOptionFound = false;
        boolean secondOptionFound = false;

        for (RushEventOptionResponseDto option : options) {
            if (option.getMainText().equals("Main Text 2") &&
                    option.getSubText().equals("Sub Text 2") &&
                    option.getResultMainText().equals("Result Main Text 2") &&
                    option.getResultSubText().equals("Result Sub Text 2") &&
                    option.getImageUrl().equals("http://example.com/image.jpg") &&
                    option.getPosition().equals(Position.RIGHT)) {
                firstOptionFound = true;
            } else if (option.getMainText().equals("Main Text 1") &&
                    option.getSubText().equals("Sub Text 1") &&
                    option.getResultMainText().equals("Result Main Text 1") &&
                    option.getResultSubText().equals("Result Sub Text 1") &&
                    option.getImageUrl().equals("http://example.com/image.jpg") &&
                    option.getPosition().equals(Position.LEFT)) {
                secondOptionFound = true;
            }
        }


        assertThat(firstOptionFound).isTrue();
        assertThat(secondOptionFound).isTrue();

    }

    @Test
    @DisplayName("선착순 이벤트 선택지 조회 테스트 - 실패 (이벤트 조회 실패)")
    void getRushEventOptionsTest_Failure_NoRushEvent() {
        //given
        given(rushEventRepository.findById(1L)).willReturn(Optional.empty());

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> adminService.getRushEventOptions(1L)
        );

        //then
        assertEquals(CustomErrorCode.NO_RUSH_EVENT, customException.getErrorCode());
        assertEquals("선착순 이벤트를 찾을 수 없습니다.", customException.getMessage());
    }

    @Test
    @DisplayName("기대평 조회 테스트 - 성공")
    void getLotteryEventExpectationsTest_Success() {
        //given
        given(lotteryParticipantsRepository.findById(1L)).willReturn(Optional.ofNullable(lotteryParticipants));

        List<CasperBot> casperBotList = new ArrayList<>();
        casperBotList.add(casperBot);
        Page<CasperBot> casperBotPage = new PageImpl<>(casperBotList);

        given(casperBotRepository.findByPhoneNumberAndActiveExpectations(eq("010-0000-0000"), any(Pageable.class)))
                .willReturn(casperBotPage);

        //when
        ExpectationsPagingResponseDto lotteryEventExpectations = adminService.getLotteryEventExpectations(0, 1, 1L);

        //then
        List<LotteryEventResponseDto> expectations = lotteryEventExpectations.expectations();

        boolean expectationFound = false;

        for (LotteryEventResponseDto exp : expectations) {
            if (exp.getExpectation().equals("expectation") &&
                    exp.getCreatedDate().equals(LocalDate.of(2000, 9, 27)) &&
                    exp.getCreatedTime().equals(LocalTime.of(0, 0))) {
                expectationFound = true;
                break;
            }
        }

        assertThat(expectationFound).isTrue();
        assertThat(lotteryEventExpectations.isLastPage()).isTrue();
        assertThat(lotteryEventExpectations.totalExpectations()).isEqualTo(1);
    }

    @Test
    @DisplayName("기대평 조회 테스트 - 실패 (참여자 조회 실패)")
    void getLotteryEventExpectationsTest_Failure_UserNotFound() {
        //given
        given(lotteryParticipantsRepository.findById(1L))
                .willReturn(Optional.empty());

        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> adminService.getLotteryEventExpectations(0, 1, 1L)
        );

        //then
        assertEquals(CustomErrorCode.USER_NOT_FOUND, customException.getErrorCode());
        assertEquals("응모하지 않은 사용자입니다.", customException.getMessage());
    }

    @Test
    @DisplayName("부적절한 기대평 삭제 테스트 - 성공")
    void deleteLotteryEventExpectationTest_Success() {
        //given
        List<CasperBotResponseDto> casperBotResponseDtoList = new ArrayList<>();
        casperBotResponseDtoList.add(CasperBotResponseDto.of(casperBot));

        given(casperBotRepository.findById(1L)).willReturn(Optional.ofNullable(casperBot));
        given(casperBotRedisTemplate.opsForList()).willReturn(listOperations);
        given(casperBotRedisTemplate.opsForList().range(anyString(), eq(0L), eq(-1L)))
                .willReturn(casperBotResponseDtoList);

        //when
        adminService.deleteLotteryEventExpectation(1L);

        //then

    }
}
