package JGS.CasperEvent.domain.event.controller.adminController;

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
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.RushEventParticipantResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.RushEventParticipantsListResponseDto;
import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
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

    private LotteryEvent lotteryEvent;
    private LotteryEventRequestDto lotteryEventRequestDto;
    private LotteryEventResponseDto lotteryEventResponseDto;
    private LotteryParticipants lotteryParticipants;
    private LotteryEventParticipantsResponseDto lotteryEventParticipantsResponseDto;
    private LotteryEventParticipantsListResponseDto lotteryEventParticipantsListResponseDto;
    private RushEventRequestDto rushEventRequestDto;
    private RushEventOptionRequestDto leftOptionRequestDto;
    private RushEventOptionRequestDto rightOptionRequestDto;
    private AdminRushEventResponseDto adminRushEventResponseDto;
    private RushEvent rushEvent;
    private RushParticipants rushParticipants;
    private RushEventParticipantResponseDto rushEventParticipantResponseDto;
    private RushEventParticipantsListResponseDto rushEventParticipantsListResponseDto;


    @BeforeEach
    void setUp() throws Exception {
        this.adminId = "adminId";
        this.password = "password";

        admin = new Admin(adminId, password, Role.ADMIN);
        given(adminService.verifyAdmin(any())).willReturn(admin);

        user = new BaseUser("010-0000-0000", Role.USER);
        user.setCreatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        user.setUpdatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
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
        this.lotteryParticipants = new LotteryParticipants(user);

        // 추첨 이벤트 참여자 응답 DTO
        this.lotteryEventParticipantsResponseDto = LotteryEventParticipantsResponseDto.of(lotteryParticipants);

        // 추첨 이벤트 참여자 리스트 응답 DTO
        List<LotteryEventParticipantsResponseDto> participants = new ArrayList<>();
        participants.add(lotteryEventParticipantsResponseDto);
        this.lotteryEventParticipantsListResponseDto = new LotteryEventParticipantsListResponseDto(participants, true, 1);

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

        // 선착순 이벤트 조회 응답 DTO
        adminRushEventResponseDto = AdminRushEventResponseDto.of(rushEvent);

        // 선착순 이벤트 참여자 엔티티
        rushParticipants = new RushParticipants(user, rushEvent, 1);
        rushParticipants.setCreatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));
        rushParticipants.setUpdatedAt(LocalDateTime.of(2000, 9, 27, 0, 0, 0));

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
                .andExpect(jsonPath("$.rushEventId").isEmpty())
                .andExpect(jsonPath("$.eventDate").value("2024-08-15"))
                .andExpect(jsonPath("$.startTime").value("00:00:00"))
                .andExpect(jsonPath("$.endTime").value("23:59:59"))
                .andExpect(jsonPath("$.winnerCount").value(315))
                .andExpect(jsonPath("$.prizeImageUrl").value("prize image url"))
                .andExpect(jsonPath("$.prizeDescription").value("prize description"))
                .andExpect(jsonPath("$.createdAt").isEmpty())
                .andExpect(jsonPath("$.updatedAt").isEmpty())
                .andExpect(jsonPath("$.status").value("AFTER"))
                .andExpect(jsonPath("$.options").isEmpty());
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
                .andExpect(jsonPath("$[0].options").isEmpty());
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
                .andExpect(jsonPath("$.participantsList[0].createdDate").value("2000-09-27"))
                .andExpect(jsonPath("$.participantsList[0].rank").value(1))
                .andExpect(jsonPath("$.participantsList[0].phoneNumber").value("010-0000-0000"))
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