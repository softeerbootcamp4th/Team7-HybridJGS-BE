package JGS.CasperEvent.domain.event.controller.adminController;

import JGS.CasperEvent.domain.event.dto.RequestDto.AdminRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto.CasperBotRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto.LotteryEventRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto.RushEventOptionRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto.RushEventRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.ImageUrlResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.*;
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.*;
import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.entity.event.RushOption;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.entity.participants.LotteryWinners;
import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import JGS.CasperEvent.domain.event.service.adminService.AdminService;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.Position;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.jwt.service.UserService;
import JGS.CasperEvent.global.jwt.util.JwtProvider;
import JGS.CasperEvent.global.response.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AdminController.class)
@Import(JwtProvider.class)
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;
    @MockBean
    private UserService userService;

    private Admin admin;
    private String adminId;
    private String password;
    private String accessToken;
    private BaseUser user;

    private CasperBotRequestDto casperBotRequestDto;
    private CasperBot casperBot;
    private LotteryEvent lotteryEvent;
    private LotteryEventRequestDto lotteryEventRequestDto;
    private LotteryEventResponseDto lotteryEventResponseDto;
    private LotteryParticipants lotteryParticipants;
    private LotteryEventParticipantsResponseDto lotteryEventParticipantsResponseDto;
    private LotteryEventParticipantsListResponseDto lotteryEventParticipantsListResponseDto;
    private LotteryEventDetailResponseDto lotteryEventDetailResponseDto;
    private LotteryEventExpectationsResponseDto lotteryEventExpectationsResponseDto;
    private LotteryEventExpectationResponseDto lotteryEventExpectationResponseDto;
    private LotteryEventWinnerListResponseDto lotteryEventWinnerListResponseDto;
    private LotteryEventWinnerResponseDto lotteryEventWinnerResponseDto;
    private LotteryWinners lotteryWinners;


    private RushEventRequestDto rushEventRequestDto;
    private RushEventOptionRequestDto leftOptionRequestDto;
    private RushEventOptionRequestDto rightOptionRequestDto;
    private AdminRushEventResponseDto adminRushEventResponseDto;
    private RushEvent rushEvent;
    private RushOption leftOption;
    private RushOption rightOption;
    private RushParticipants rushParticipants;
    private RushEventParticipantResponseDto rushEventParticipantResponseDto;
    private RushEventParticipantsListResponseDto rushEventParticipantsListResponseDto;


    @BeforeEach
    void setUp() throws Exception {
        this.adminId = "adminId";
        this.password = "password";

        admin = new Admin(adminId, password, Role.ADMIN);
        given(adminService.verifyAdmin(any())).willReturn(admin);

        user = spy(new BaseUser("010-0000-0000", Role.USER));
        lenient().when(user.getCreatedAt()).thenReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        lenient().when(user.getUpdatedAt()).thenReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        given(userService.verifyUser(any())).willReturn(user);
        // 엑세스 토큰 설정
        this.accessToken = getToken(adminId, password);

        // 추첨 이벤트 설정
        this.lotteryEvent = new LotteryEvent(LocalDateTime.of(2000, 9, 27, 0, 0, 0), LocalDateTime.of(2100, 9, 27, 0, 0, 0), 315);

        // 추첨 이벤트 생성 DTO
        this.lotteryEventRequestDto = LotteryEventRequestDto.builder()
                .startDate(LocalDate.of(2000, 9, 27))
                .startTime(LocalTime.of(0, 0, 0))
                .endDate(LocalDate.of(2100, 9, 27))
                .endTime(LocalTime.of(0, 0, 0))
                .winnerCount(315)
                .build();

        // 추첨 이벤트 응답 DTO
        this.lotteryEventResponseDto = LotteryEventResponseDto.of(lotteryEvent, LocalDateTime.of(2024, 8, 15, 0, 0, 0));

        // 추첨 이벤트 참여자 객체
        LotteryParticipants realLotteryParticipants = new LotteryParticipants(user);
        this.lotteryParticipants = spy(realLotteryParticipants);
        doReturn(1L).when(lotteryParticipants).getId();


        // 추첨 이벤트 참여자 응답 DTO
        this.lotteryEventParticipantsResponseDto = LotteryEventParticipantsResponseDto.of(lotteryParticipants);

        // 추첨 이벤트 참여자 리스트 응답 DTO
        List<LotteryEventParticipantsResponseDto> participants = new ArrayList<>();
        participants.add(lotteryEventParticipantsResponseDto);
        this.lotteryEventParticipantsListResponseDto = new LotteryEventParticipantsListResponseDto(participants, true, 1);

        // 추첨 이벤트 상세 응답 DTO
        lotteryEventDetailResponseDto = LotteryEventDetailResponseDto.of(lotteryEvent);

        // 캐스퍼 봇
        casperBotRequestDto = CasperBotRequestDto.builder()
                .eyeShape(0)
                .eyePosition(0)
                .mouthShape(0)
                .color(0)
                .sticker(0)
                .name("name")
                .expectation("expectation")
                .referralId("QEszP1K8IqcapUHAVwikXA==").build();

        casperBot = spy(new CasperBot(casperBotRequestDto, "010-0000-0000"));
        lenient().when(casperBot.getCreatedAt()).thenReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        lenient().when(casperBot.getUpdatedAt()).thenReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0));

        // 추첨 이벤트 당첨자 엔티티
        lotteryWinners = spy(new LotteryWinners(lotteryParticipants));
        doReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0)).when(lotteryWinners).getCreatedAt();
        doReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0)).when(lotteryWinners).getUpdatedAt();


        // 추첨 이벤트 당첨자 응답 DTO
        lotteryEventWinnerResponseDto = LotteryEventWinnerResponseDto.of(lotteryWinners);

        // 추첨 이벤트 당첨자 리스트 응답 DTO
        List<LotteryEventWinnerResponseDto> lotteryEventWinnerResponseDtoList = new ArrayList<>();
        lotteryEventWinnerResponseDtoList.add(lotteryEventWinnerResponseDto);
        lotteryEventWinnerListResponseDto = new LotteryEventWinnerListResponseDto(lotteryEventWinnerResponseDtoList, true, 1);

        // 추첨 이벤트 기대평 응답 DTO
        lotteryEventExpectationResponseDto = LotteryEventExpectationResponseDto.of(casperBot);

        // 추첨 이벤트 기대평 리스트 응답 DTO
        List<LotteryEventExpectationResponseDto> lotteryEventExpectationResponseDtoList = new ArrayList<>();
        lotteryEventExpectationResponseDtoList.add(lotteryEventExpectationResponseDto);
        lotteryEventExpectationsResponseDto = new LotteryEventExpectationsResponseDto(lotteryEventExpectationResponseDtoList, true, 1);


        // 선착순 이벤트 왼쪽 옵션
        leftOptionRequestDto = RushEventOptionRequestDto.builder()
                .rushOptionId(1L)
                .position(Position.LEFT)
                .mainText("main text 1")
                .subText("sub text 1")
                .resultMainText("result main text 1")
                .resultSubText("result sub text 1")
                .imageUrl("image url 1").build();

        // 선착순 이벤트 오른쪽 옵션
        rightOptionRequestDto = RushEventOptionRequestDto.builder()
                .rushOptionId(1L)
                .position(Position.RIGHT)
                .mainText("main text 2")
                .subText("sub text 2")
                .resultMainText("result main text 2")
                .resultSubText("result sub text 2")
                .imageUrl("image url 2").build();


        // 선착순 이벤트 생성 요청 DTO
        Set<RushEventOptionRequestDto> options = new HashSet<>();
        options.add(leftOptionRequestDto);
        options.add(rightOptionRequestDto);

        this.rushEventRequestDto = RushEventRequestDto.builder()
                .rushEventId(1L)
                .eventDate(LocalDate.of(2024, 8, 15))
                .startTime(LocalTime.of(0, 0, 0))
                .endTime(LocalTime.of(23, 59, 59))
                .winnerCount(315)
                .prizeImageUrl("prize img")
                .prizeDescription("prize description")
                .options(options).build();

        // 선착순 이벤트
        rushEvent = new RushEvent(
                LocalDateTime.of(2024, 8, 15, 0, 0, 0),
                LocalDateTime.of(2024, 8, 15, 23, 59, 59),
                315,
                "prize image url",
                "prize description"
        );

        leftOption = RushOption.builder()
                .optionId(1L)
                .rushEvent(rushEvent)
                .mainText("main text 1")
                .subText("sub text 1")
                .resultMainText("result main text 1")
                .resultSubText("result sub text 1")
                .imageUrl("image url 1")
                .position(Position.LEFT).build();

        rightOption = RushOption.builder()
                .optionId(2L)
                .rushEvent(rushEvent)
                .mainText("main text 2")
                .subText("sub text 2")
                .resultMainText("result main text 2")
                .resultSubText("result sub text 2")
                .imageUrl("image url 2")
                .position(Position.RIGHT).build();


        rushEvent.addOption(leftOption, rightOption);

        // 선착순 이벤트 조회 응답 DTO
        adminRushEventResponseDto = AdminRushEventResponseDto.of(rushEvent);

        // 선착순 이벤트 참여자 엔티티
        rushParticipants = spy(new RushParticipants(user, rushEvent, 1));
        lenient().when(rushParticipants.getCreatedAt()).thenReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        lenient().when(rushParticipants.getUpdatedAt()).thenReturn(LocalDateTime.of(2000, 9, 27, 0, 0, 0));

        // 선착순 이벤트 참여자 응답 DTO
        rushEventParticipantResponseDto = RushEventParticipantResponseDto.of(rushParticipants, 1L);

        // 선착순 이벤트 참여자 리스트 조회 응답 DTO
        List<RushEventParticipantResponseDto> rushEventParticipantResponseDtoList = new ArrayList<>();
        rushEventParticipantResponseDtoList.add(rushEventParticipantResponseDto);
        rushEventParticipantsListResponseDto = new RushEventParticipantsListResponseDto(rushEventParticipantResponseDtoList, true, 1);
    }


    @Test
    @DisplayName("어드민 생성 성공 테스트")
    void postAdminSuccessTest() throws Exception {
        //given
        AdminRequestDto adminRequestDto = AdminRequestDto.builder().adminId(adminId).password(password).build();
        String requestBody = objectMapper.writeValueAsString(adminRequestDto);

        given(adminService.postAdmin(adminRequestDto)).willReturn(ResponseDto.of("관리자 생성 성공"));
        //when
        ResultActions perform = mockMvc.perform(post("/admin/join").contentType(APPLICATION_JSON).content(requestBody));

        //then
        perform.andExpect(status().isCreated()).andExpect(jsonPath("$.message").value("관리자 생성 성공")).andDo(print());
    }

    @Test
    @DisplayName("이미지 업로드 성공 테스트")
    void postImageSuccessTest() throws Exception {
        //given
        given(adminService.postImage(any())).willReturn(new ImageUrlResponseDto("https://image.url"));
        MockMultipartFile image = new MockMultipartFile("image", "image.png", "png", "<<data>>".getBytes());
        //when
        ResultActions perform = mockMvc.perform(multipart("/admin/image").file(image).header("Authorization", accessToken)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        //then
        perform.andExpect(status().isCreated()).andExpect(jsonPath("$.imageUrl").value("https://image.url")).andDo(print());
    }

    @Test
    @DisplayName("추첨 이벤트 조회 성공 테스트")
    void getLotteryEventSuccessTest() throws Exception {
        //given
        given(adminService.getLotteryEvent()).willReturn(LotteryEventDetailResponseDto.of(lotteryEvent));

        //when
        ResultActions perform = mockMvc.perform(get("/admin/event/lottery").header("Authorization", accessToken).contentType(APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value("2000-09-27"))
                .andExpect(jsonPath("$.startTime").value("00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2100-09-27"))
                .andExpect(jsonPath("$.endTime").value("00:00:00"))
                .andExpect(jsonPath("$.appliedCount").value(0))
                .andExpect(jsonPath("$.winnerCount").value(315))
                .andExpect(jsonPath("$.status").value("DURING"))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 이벤트 생성 성공 테스트")
    void createLotteryEventSuccessTest() throws Exception {
        //given
        given(adminService.createLotteryEvent(lotteryEventRequestDto)).willReturn(lotteryEventResponseDto);
        String requestBody = objectMapper.writeValueAsString(lotteryEventRequestDto);
        //when
        ResultActions perform = mockMvc.perform(post("/admin/event/lottery")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON)
                .content(requestBody));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.serverDateTime").value("2024-08-15T00:00:00"))
                .andExpect(jsonPath("$.eventStartDate").value("2000-09-27T00:00:00"))
                .andExpect(jsonPath("$.eventEndDate").value("2100-09-27T00:00:00"))
                .andExpect(jsonPath("$.activePeriod").value("36524"))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 이벤트 참여자 조회 성공 테스트")
    void getLotteryEventParticipantsSuccessTest() throws Exception {
        //given
        given(adminService.getLotteryEventParticipants(anyInt(), anyInt(), anyString()))
                .willReturn(lotteryEventParticipantsListResponseDto);
        //when
        ResultActions perform = mockMvc.perform(get("/admin/event/lottery/participants")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.participantsList[0].phoneNumber").value("010-0000-0000"))
                .andExpect(jsonPath("$.participantsList[0].linkClickedCounts").value(0))
                .andExpect(jsonPath("$.participantsList[0].expectation").value(0))
                .andExpect(jsonPath("$.participantsList[0].appliedCount").value(1))
                .andExpect(jsonPath("$.participantsList[0].createdDate").value("2000-09-27"))
                .andExpect(jsonPath("$.participantsList[0].createdTime").value("00:00:00"))
                .andExpect(jsonPath("$.isLastPage").value(true))
                .andExpect(jsonPath("$.totalParticipants").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("선착순 이벤트 생성 성공 이벤트")
    void createRushEventSuccessTest() throws Exception {
        //given
        given(adminService.createRushEvent(any(), any(), any(), any()))
                .willReturn(adminRushEventResponseDto);
        String requestDto = objectMapper.writeValueAsString(rushEventRequestDto);
        //when
        MockMultipartFile dto = new MockMultipartFile("dto", "dto", "application/json", requestDto.getBytes());
        MockMultipartFile prizeImage = new MockMultipartFile("prizeImg", "prizeImage.png", "image/png", "<<data>>".getBytes());
        MockMultipartFile leftOptionImg = new MockMultipartFile("leftOptionImg", "leftOptionImg.png", "image/png", "<<data>>".getBytes());
        MockMultipartFile rightOptionImg = new MockMultipartFile("rightOptionImg", "rightOptionImg.png", "image/png", "<<data>>".getBytes());

        ResultActions perform = mockMvc.perform(multipart("/admin/event/rush")
                .file(dto)
                .file(prizeImage)
                .file(leftOptionImg)
                .file(rightOptionImg)
                .header("Authorization", accessToken)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventDate").value("2024-08-15"))
                .andExpect(jsonPath("$.startTime").value("00:00:00"))
                .andExpect(jsonPath("$.endTime").value("23:59:59"))
                .andExpect(jsonPath("$.winnerCount").value(315))
                .andExpect(jsonPath("$.prizeImageUrl").value("prize image url"))
                .andExpect(jsonPath("$.prizeDescription").value("prize description"))
                .andExpect(jsonPath("$.status").value("AFTER"))
                .andExpect(jsonPath("$.options[0].optionId").value(1))
                .andExpect(jsonPath("$.options[0].mainText").value("main text 1"))
                .andExpect(jsonPath("$.options[0].subText").value("sub text 1"))
                .andExpect(jsonPath("$.options[0].resultMainText").value("result main text 1"))
                .andExpect(jsonPath("$.options[0].resultSubText").value("result sub text 1"))
                .andExpect(jsonPath("$.options[0].imageUrl").value("image url 1"))
                .andExpect(jsonPath("$.options[0].position").value("LEFT"))
                .andExpect(jsonPath("$.options[1].optionId").value(2))
                .andExpect(jsonPath("$.options[1].mainText").value("main text 2"))
                .andExpect(jsonPath("$.options[1].subText").value("sub text 2"))
                .andExpect(jsonPath("$.options[1].resultMainText").value("result main text 2"))
                .andExpect(jsonPath("$.options[1].resultSubText").value("result sub text 2"))
                .andExpect(jsonPath("$.options[1].imageUrl").value("image url 2"))
                .andExpect(jsonPath("$.options[1].position").value("RIGHT"))

                .andDo(print());
    }

    @Test
    @DisplayName("선착순 이벤트 전체 조회 성공 테스트")
    void getRushEventsSuccessTest() throws Exception {
        //given
        List<AdminRushEventResponseDto> rushEvents = new ArrayList<>();
        rushEvents.add(adminRushEventResponseDto);
        given(adminService.getRushEvents()).willReturn(rushEvents);

        //when
        ResultActions perform = mockMvc.perform(get("/admin/event/rush")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rushEventId").isEmpty())
                .andExpect(jsonPath("$[0].eventDate").value("2024-08-15"))
                .andExpect(jsonPath("$[0].startTime").value("00:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("23:59:59"))
                .andExpect(jsonPath("$[0].winnerCount").value(315))
                .andExpect(jsonPath("$[0].prizeImageUrl").value("prize image url"))
                .andExpect(jsonPath("$[0].prizeDescription").value("prize description"))
                .andExpect(jsonPath("$[0].createdAt").isEmpty())
                .andExpect(jsonPath("$[0].updatedAt").isEmpty())
                .andExpect(jsonPath("$[0].status").value("AFTER"))
                .andExpect(jsonPath("$[0].options").isArray())
                .andDo(print());
    }

    @Test
    @DisplayName("선착순 이벤트 참여자 조회 성공 테스트")
    void getRushEventParticipantsSuccessTest() throws Exception {
        //given
        given(adminService.getRushEventParticipants(anyLong(), anyInt(), anyInt(), anyInt(), anyString()))
                .willReturn(rushEventParticipantsListResponseDto);

        //when
        ResultActions perform = mockMvc.perform(get("/admin/event/rush/1/participants")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.participantsList[0].phoneNumber").value("010-0000-0000"))
                .andExpect(jsonPath("$.participantsList[0].balanceGameChoice").value(1))
                .andExpect(jsonPath("$.participantsList[0].createdDate").value("2000-09-27"))
                .andExpect(jsonPath("$.participantsList[0].createdTime").value("00:00:00"))
                .andExpect(jsonPath("$.participantsList[0].rank").value(1))
                .andExpect(jsonPath("$.isLastPage").value(true))
                .andExpect(jsonPath("$.totalParticipants").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("선착순 이벤트 당첨자 조회 성공 테스트")
    void getRushEventWinnersSuccessTest() throws Exception {
        //given
        given(adminService.getRushEventWinners(anyLong(), anyInt(), anyInt(), anyString()))
                .willReturn(rushEventParticipantsListResponseDto);

        //when
        ResultActions perform = mockMvc.perform(get("/admin/event/rush/1/winner")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.participantsList[0].createdDate").value("2000-09-27"))
                .andExpect(jsonPath("$.participantsList[0].rank").value(1))
                .andExpect(jsonPath("$.participantsList[0].phoneNumber").value("010-0000-0000"))
                .andExpect(jsonPath("$.isLastPage").value(true))
                .andExpect(jsonPath("$.totalParticipants").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("선착순 이벤트 수정 성공 테스트")
    void updateRushEventSuccessTest() throws Exception {
        //given
        List<RushEventRequestDto> rushEventRequestDtoList = new ArrayList<>();
        rushEventRequestDtoList.add(rushEventRequestDto);

        List<AdminRushEventResponseDto> adminRushEventResponseDtoList = new ArrayList<>();
        adminRushEventResponseDtoList.add(adminRushEventResponseDto);

        given(adminService.updateRushEvents(anyList()))
                .willReturn(adminRushEventResponseDtoList);

        String requestBody = objectMapper.writeValueAsString(rushEventRequestDtoList);
        //when
        ResultActions perform = mockMvc.perform(put("/admin/event/rush")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON)
                .content(requestBody));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventDate").value("2024-08-15"))
                .andExpect(jsonPath("$[0].startTime").value("00:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("23:59:59"))
                .andExpect(jsonPath("$[0].winnerCount").value(315))
                .andExpect(jsonPath("$[0].prizeImageUrl").value("prize image url"))
                .andExpect(jsonPath("$[0].prizeDescription").value("prize description"))
                .andExpect(jsonPath("$[0].status").value("AFTER"))
                .andDo(print());
    }

    @Test
    @DisplayName("선착순 이벤트 삭제 성공 테스트")
    void deleteRushEventSuccessTest() throws Exception {
        //given
        ResponseDto responseDto = new ResponseDto("요청에 성공하였습니다.");
        given(adminService.deleteRushEvent(1L)).willReturn(responseDto);

        //when
        ResultActions perform = mockMvc.perform(delete("/admin/event/rush/1")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("선착순 이벤트 선택지 조회 성공 테스트")
    void getRushEventOptionsSuccessTest() throws Exception {
        //given
        AdminRushEventOptionResponseDto adminRushEventOptionResponseDto = AdminRushEventOptionResponseDto.of(rushEvent);
        given(adminService.getRushEventOptions(1L))
                .willReturn(adminRushEventOptionResponseDto);
        //when
        ResultActions perform = mockMvc.perform(get("/admin/event/rush/1/options")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.options[0].optionId").value(2))
                .andExpect(jsonPath("$.options[0].mainText").value("main text 2"))
                .andExpect(jsonPath("$.options[0].subText").value("sub text 2"))
                .andExpect(jsonPath("$.options[0].resultMainText").value("result main text 2"))
                .andExpect(jsonPath("$.options[0].resultSubText").value("result sub text 2"))
                .andExpect(jsonPath("$.options[0].imageUrl").value("image url 2"))
                .andExpect(jsonPath("$.options[0].position").value("RIGHT"))
                .andExpect(jsonPath("$.options[1].optionId").value(1))
                .andExpect(jsonPath("$.options[1].mainText").value("main text 1"))
                .andExpect(jsonPath("$.options[1].subText").value("sub text 1"))
                .andExpect(jsonPath("$.options[1].resultMainText").value("result main text 1"))
                .andExpect(jsonPath("$.options[1].resultSubText").value("result sub text 1"))
                .andExpect(jsonPath("$.options[1].imageUrl").value("image url 1"))
                .andExpect(jsonPath("$.options[1].position").value("LEFT"))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 이벤트 삭제 성공 테스트")
    void deleteLotteryEventSuccessTest() throws Exception {
        //given

        //when
        ResultActions perform = mockMvc.perform(delete("/admin/event/lottery")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON));

        //then
        perform.andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 이벤트 수정 성공 테스트")
    void updateLotteryEventSuccessTest() throws Exception {
        //given
        given(adminService.updateLotteryEvent(lotteryEventRequestDto))
                .willReturn(lotteryEventDetailResponseDto);
        String requestBody = objectMapper.writeValueAsString(lotteryEventRequestDto);

        //when
        ResultActions perform = mockMvc.perform(put("/admin/event/lottery")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON)
                .content(requestBody));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value("2000-09-27"))
                .andExpect(jsonPath("$.startTime").value("00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2100-09-27"))
                .andExpect(jsonPath("$.endTime").value("00:00:00"))
                .andExpect(jsonPath("$.appliedCount").value(0))
                .andExpect(jsonPath("$.winnerCount").value(315))
                .andExpect(jsonPath("$.status").value("DURING"))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 이벤트 특정 사용자의 기대평 조회 성공 테스트")
    void getLotteryEventExpectationsSuccessTest() throws Exception {
        //given
        given(adminService.getLotteryEventExpectations(anyInt(), anyInt(), anyLong()))
                .willReturn(lotteryEventExpectationsResponseDto);

        //when
        ResultActions perform = mockMvc.perform(get("/admin/event/lottery/participants/1/expectations")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.expectations[0].expectation").value("expectation"))
                .andExpect(jsonPath("$.expectations[0].createdDate").value("2000-09-27"))
                .andExpect(jsonPath("$.expectations[0].createdTime").value("00:00:00"))
                .andExpect(jsonPath("$.isLastPage").value(true))
                .andExpect(jsonPath("$.totalExpectations").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 이벤트 특정 기대평 삭제 성공 테스트")
    void deleteLotteryEventExpectationSuccessTest() throws Exception {
        //given

        //when
        ResultActions perform = mockMvc.perform(patch("/admin/event/lottery/expectations/1")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON));

        //then
        perform.andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 이벤트 당첨자 추첨 성공 테스트")
    void pickLotteryEventWinnerSuccessTest() throws Exception {
        //given
        given(adminService.pickLotteryEventWinners())
                .willReturn(ResponseDto.of("추첨이 완료되었습니다."));

        //when
        ResultActions perform = mockMvc.perform(post("/admin/event/lottery/winner")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("추첨이 완료되었습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 이벤트 당첨자 삭제 성공 테스트")
    void deleteLotteryEventWinners() throws Exception {
        //given
        given(adminService.deleteLotteryEventWinners())
                .willReturn(ResponseDto.of("당첨자 명단을 삭제했습니다."));

        //when
        ResultActions perform = mockMvc.perform(delete("/admin/event/lottery/winner")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("당첨자 명단을 삭제했습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("추첨 이벤트 당첨자 조회 성공 테스트")
    void getWinnersSuccessTest() throws Exception {
        //given
        given(adminService.getLotteryEventWinners(anyInt(), anyInt(), anyString()))
                .willReturn(lotteryEventWinnerListResponseDto);

        //when
        ResultActions perform = mockMvc.perform(get("/admin/event/lottery/winner")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.participantsList[0].id").value(1))
                .andExpect(jsonPath("$.participantsList[0].phoneNumber").value("010-0000-0000"))
                .andExpect(jsonPath("$.participantsList[0].linkClickedCounts").value(0))
                .andExpect(jsonPath("$.participantsList[0].expectation").value(0))
                .andExpect(jsonPath("$.participantsList[0].appliedCount").value(1))
                .andExpect(jsonPath("$.participantsList[0].ranking").value(0))
                .andExpect(jsonPath("$.participantsList[0].createdDate").value("2000-09-27"))
                .andExpect(jsonPath("$.participantsList[0].createdTime").value("00:00:00"))
                .andExpect(jsonPath("$.isLastPage").value(true))
                .andExpect(jsonPath("$.totalParticipants").value(1))
                .andDo(print());
    }

    String getToken(String id, String password) throws Exception {
        String requestBody = String.format("""
                {
                    "adminId": "%s",
                    "password": "%s"
                }
                """, id, password);

        ResultActions perform = mockMvc.perform(post("/admin/auth").contentType(APPLICATION_JSON).content(requestBody));

        String jsonString = perform.andReturn().getResponse().getContentAsString();
        String tokenPrefix = "\"accessToken\":\"";
        int start = jsonString.indexOf(tokenPrefix) + tokenPrefix.length();
        int end = jsonString.indexOf("\"", start);

        return "Bearer " + jsonString.substring(start, end);
    }
}