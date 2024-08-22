package JGS.CasperEvent.domain.event.controller.eventController;

import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.*;
import JGS.CasperEvent.domain.event.dto.response.rush.RushEventResponseDto;
import JGS.CasperEvent.domain.event.service.adminService.AdminService;
import JGS.CasperEvent.domain.event.service.eventService.RushEventService;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.jwt.service.UserService;
import JGS.CasperEvent.global.jwt.util.JwtProvider;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RushEventController.class)
class RushEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RushEventService rushEventService;

    @MockBean
    private UserService userService;

    @MockBean
    private AdminService adminService;

    private String phoneNumber;
    private String accessToken;

    @TestConfiguration
    static class TestConfig{
        @Bean
        public JwtProvider jwtProvider(){
            String secretKey = "mockKEymockKEymockKEymockKEymockKEymockKEymockKEy";
            byte[] secret = secretKey.getBytes();
            return new JwtProvider(Keys.hmacShaKeyFor(secret));
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        this.phoneNumber = "010-0000-0000";

        // userService 모킹
        given(userService.verifyUser(any())).willReturn(new BaseUser(this.phoneNumber, Role.USER));

        // 액세스 토큰 설정
        this.accessToken = getToken(this.phoneNumber);

        // Mock 데이터 설정
        RushEventListResponseDto rushEventListResponseDto = new RushEventListResponseDto(
                Arrays.asList(
                        RushEventResponseDto.withMain(37L, LocalDateTime.of(2024, 8, 11, 22, 0), LocalDateTime.of(2024, 8, 11, 22, 10)),
                        RushEventResponseDto.withMain(38L, LocalDateTime.of(2024, 8, 12, 22, 0), LocalDateTime.of(2024, 8, 12, 22, 10)),
                        RushEventResponseDto.withMain(39L, LocalDateTime.of(2024, 8, 13, 22, 0), LocalDateTime.of(2024, 8, 13, 22, 10)),
                        RushEventResponseDto.withMain(40L, LocalDateTime.of(2024, 8, 14, 22, 0), LocalDateTime.of(2024, 8, 14, 22, 10)),
                        RushEventResponseDto.withMain(41L, LocalDateTime.of(2024, 8, 15, 22, 0), LocalDateTime.of(2024, 8, 15, 22, 10)),
                        RushEventResponseDto.withMain(42L, LocalDateTime.of(2024, 8, 16, 22, 0), LocalDateTime.of(2024, 8, 16, 22, 10))
                ),
                LocalDateTime.of(2024, 8, 12, 13, 46, 29, 48782),
                37L,
                LocalDate.of(2024, 8, 11),
                LocalDate.of(2024, 8, 16),
                6L
        );

        given(rushEventService.getAllRushEvents()).willReturn(rushEventListResponseDto);

        MainRushEventOptionsResponseDto mainRushEventOptionsResponseDto = new MainRushEventOptionsResponseDto(
                new MainRushEventOptionResponseDto("leftMainText", "leftSubText"),
                new MainRushEventOptionResponseDto("rightMainText", "rightSubText")
        );

        given(rushEventService.getTodayRushEventOptions()).willReturn(mainRushEventOptionsResponseDto);

        ResultRushEventOptionResponseDto resultRushEventOptionResponseDto = new ResultRushEventOptionResponseDto(
                "mainText",
                "resultMainText",
                "resultSubText"
        );

        given(rushEventService.getRushEventOptionResult(1)).willReturn(resultRushEventOptionResponseDto);
        given(rushEventService.getRushEventOptionResult(2)).willReturn(resultRushEventOptionResponseDto);

        // 예: apply 메서드가 정상적으로 동작하는 경우 (optionId가 2일 때)
        willDoNothing().given(rushEventService).apply(any(BaseUser.class), eq(2));

        // 예: apply 메서드가 실패하는 경우 (optionId가 1일 때)
        willThrow(new CustomException("이미 응모한 회원입니다.", CustomErrorCode.CONFLICT))
                .given(rushEventService).apply(any(BaseUser.class), eq(1));

        RushEventRateResponseDto rushEventRateResponseDto = new RushEventRateResponseDto(
                1,
                315L,
                1000L
        );

        given(rushEventService.getRushEventRate(any())).willReturn(rushEventRateResponseDto);

        RushEventResultResponseDto rushEventResultResponseDto = new RushEventResultResponseDto(
                1,
                315L,
                1000L,
                1L,
                1000L,
                true
        );

        given(rushEventService.getRushEventResult(any())).willReturn(rushEventResultResponseDto);
    }

    @Test
    @DisplayName("메인화면 선착순 이벤트 전체 조회 API 테스트")
    void getRushEventListAndServerTime() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(get("/event/rush")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.events").isArray())
                .andExpect(jsonPath("$.events.length()").value(6))
                .andExpect(jsonPath("$.events[0].rushEventId").value(37))
                .andExpect(jsonPath("$.serverTime").value("2024-08-12T13:46:29.000048782"))
                .andExpect(jsonPath("$.todayEventId").value(37))
                .andExpect(jsonPath("$.eventStartDate").value("2024-08-11"))
                .andExpect(jsonPath("$.eventEndDate").value("2024-08-16"))
                .andExpect(jsonPath("$.activePeriod").value(6))
                .andDo(print());
    }

    @Test
    @DisplayName("오늘의 선착순 이벤트 조회 API 성공 테스트")
    void getTodayEventTest() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(get("/event/rush/today")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.leftOption.mainText").value("leftMainText"))
                .andExpect(jsonPath("$.leftOption.subText").value("leftSubText"))
                .andExpect(jsonPath("$.rightOption.mainText").value("rightMainText"))
                .andExpect(jsonPath("$.rightOption.subText").value("rightSubText"))
                .andDo(print());
    }


    @Test
    @DisplayName("응모 성공 테스트 - Option ID 2")
    void applyRushEvent_Success() throws Exception {
        int optionId = 2;

        ResultActions perform = mockMvc.perform(post("/event/rush/options/{optionId}/apply", optionId)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isNoContent())  // 204 No Content 응답 확인
                .andDo(print());
    }

    @Test
    @DisplayName("응모 실패 테스트 - Option ID 1")
    void applyRushEvent_Failure_AlreadyApplied() throws Exception {
        int optionId = 1;

        ResultActions perform = mockMvc.perform(post("/event/rush/options/{optionId}/apply", optionId)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isConflict())  // 409 Conflict 응답 확인
                .andExpect(jsonPath("$.errorCode").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("이미 응모한 회원입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("선택지 결과 조회 성공 테스트")
    void getResultOptionTest() throws Exception {
        // given
        int optionId = 1;

        // when
        ResultActions perform = mockMvc.perform(get("/event/rush/options/{optionId}/result", optionId)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.mainText").value("mainText"))
                .andExpect(jsonPath("$.resultMainText").value("resultMainText"))
                .andExpect(jsonPath("$.resultSubText").value("resultSubText"))
                .andDo(print());
    }

    @Test
    @DisplayName("밸런스 게임 비율 조회 API 테스트")
    void getRushEventRateTest() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(get("/event/rush/balance")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.optionId").value(1))
                .andExpect(jsonPath("$.leftOption").value(315))
                .andExpect(jsonPath("$.rightOption").value(1000))
                .andDo(print());
    }

    @Test
    @DisplayName("밸런스 게임 최종 결과 조회 API 테스트")
    void getRushEventResultTest() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(get("/event/rush/result")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.optionId").value(1))
                .andExpect(jsonPath("$.leftOption").value(315))
                .andExpect(jsonPath("$.rightOption").value(1000))
                .andExpect(jsonPath("$.rank").value(1))
                .andExpect(jsonPath("$.totalParticipants").value(1000))
                .andExpect(jsonPath("$.isWinner").value(true))
                .andDo(print());
    }

    private String getToken(String phoneNumber) throws Exception {
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
