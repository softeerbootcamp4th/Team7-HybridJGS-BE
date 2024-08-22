package JGS.CasperEvent.domain.event.controller.eventController;

import JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto.CasperBotRequestDto;
import JGS.CasperEvent.domain.event.dto.response.lottery.CasperBotResponseDto;
import JGS.CasperEvent.domain.event.dto.response.lottery.LotteryEventParticipantResponseDto;
import JGS.CasperEvent.domain.event.dto.response.lottery.LotteryEventResponseDto;
import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.service.adminService.AdminService;
import JGS.CasperEvent.domain.event.service.eventService.LotteryEventService;
import JGS.CasperEvent.domain.event.service.redisService.RedisService;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.jwt.service.UserService;
import JGS.CasperEvent.global.jwt.util.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LotteryEventController.class)
class LotteryEventControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private LotteryEventService lotteryEventService;
    @MockBean
    private UserService userService;
    @MockBean
    private AdminService adminService;
    @MockBean
    private RedisService redisService;

    private BaseUser user;
    private String phoneNumber;
    private String accessToken;
    private CasperBot casperBot;
    private CasperBotRequestDto casperBotRequest;
    private CasperBotResponseDto casperBotResponse;
    private LotteryParticipants lotteryParticipants;
    private LotteryEventResponseDto lotteryEventResponseDto;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtProvider jwtProvider() {
            String secretKey = "mockKEymockKEymockKEymockKEymockKEymockKEymockKEy";
            byte[] secret = secretKey.getBytes();
            return new JwtProvider(Keys.hmacShaKeyFor(secret));
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        this.phoneNumber = "010-0000-0000";

        // 베이스 유저 생성
        user = new BaseUser(this.phoneNumber, Role.USER);
        // 추첨 이벤트 참여자
        lotteryParticipants = new LotteryParticipants(user);
        // userService 모킹
        given(userService.verifyUser(any())).willReturn(user);

        // 엑세스 토큰 설정
        this.accessToken = getToken(this.phoneNumber);

        // 추첨 이벤트 조회
        LotteryEvent lotteryEvent = new LotteryEvent(
                LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                LocalDateTime.of(2024, 8, 31, 0, 0, 0),
                315
        );
        lotteryEventResponseDto = LotteryEventResponseDto.of(
                lotteryEvent,
                LocalDateTime.of(2024, 8, 1, 0, 0, 0)
        );

        casperBotRequest = CasperBotRequestDto.builder()
                .eyeShape(0)
                .eyePosition(0)
                .mouthShape(0)
                .color(0)
                .sticker(0)
                .name("name")
                .expectation("expectation")
                .referralId("QEszP1K8IqcapUHAVwikXA==")
                .build();

        casperBot = new CasperBot(casperBotRequest, "010-0000-0000");

        casperBotResponse = CasperBotResponseDto.of(casperBot);
    }

    @Test
    @DisplayName("추첨 이벤트 조회 API 성공 테스트")
    void getLotteryEventAndServerTime() throws Exception {
        //given
        given(lotteryEventService.getLotteryEvent()).willReturn(lotteryEventResponseDto);

        //when
        ResultActions perform = mockMvc.perform(get("/event/lottery")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.serverDateTime").value("2024-08-01T00:00:00"))
                .andExpect(jsonPath("$.eventStartDate").value("2024-08-01T00:00:00"))
                .andExpect(jsonPath("$.eventEndDate").value("2024-08-31T00:00:00"))
                .andDo(print());
    }

    @Test
    @DisplayName("캐스퍼 봇 생성 API 성공 테스트")
    void postCasperBot() throws Exception {
        //given
        String requestBody = objectMapper.writeValueAsString(casperBotRequest);
        given(lotteryEventService.postCasperBot(user, casperBotRequest))
                .willReturn(CasperBotResponseDto.of(casperBot));

        //when
        ResultActions perform = mockMvc.perform(post("/event/lottery/casperBot")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.eyeShape").value(0))
                .andExpect(jsonPath("$.eyePosition").value(0))
                .andExpect(jsonPath("$.mouthShape").value(0))
                .andExpect(jsonPath("$.color").value(0))
                .andExpect(jsonPath("$.sticker").value(0))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.expectation").value("expectation"))
                .andDo(print());
    }

    @Test
    @DisplayName("응모 여부 조회 성공 테스트")
    void getLotteryParticipantsSuccessTest() throws Exception {
        //given
        given(lotteryEventService.getLotteryParticipant(user))
                .willReturn(LotteryEventParticipantResponseDto.of(lotteryParticipants));

        //when
        ResultActions perform = mockMvc.perform(get("/event/lottery/applied")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.linkClickedCount").value(0))
                .andExpect(jsonPath("$.expectations").value(0))
                .andExpect(jsonPath("$.appliedCount").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("최근 100개 캐스퍼 봇 조회 성공 테스트")
    void getCasperBotsSuccessTest() throws Exception {
        //given
        List<CasperBotResponseDto> recentData = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            recentData.add(casperBotResponse);
        }
        given(redisService.getRecentData())
                .willReturn(recentData);

        //when
        ResultActions perform = mockMvc.perform(get("/event/lottery/caspers")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(100))
                .andDo(print());

    }

    String getToken(String phoneNumber) throws Exception {
        String requestBody = String.format("""
                {
                    "phoneNumber": "%s"
                }
                """, phoneNumber);

        ResultActions perform = mockMvc.perform(post("/event/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        String jsonString = perform.andReturn().getResponse().getContentAsString();
        String tokenPrefix = "\"accessToken\":\"";
        int start = jsonString.indexOf(tokenPrefix) + tokenPrefix.length();
        int end = jsonString.indexOf("\"", start);

        return "Bearer " + jsonString.substring(start, end);
    }
}