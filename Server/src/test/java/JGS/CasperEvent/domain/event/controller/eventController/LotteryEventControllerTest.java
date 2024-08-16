package JGS.CasperEvent.domain.event.controller.eventController;

import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventResponseDto;
import JGS.CasperEvent.domain.event.service.adminService.AdminService;
import JGS.CasperEvent.domain.event.service.eventService.LotteryEventService;
import JGS.CasperEvent.domain.event.service.redisService.RedisService;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.jwt.service.UserService;
import JGS.CasperEvent.global.jwt.util.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LotteryEventController.class)
@Import(JwtProvider.class)
public class LotteryEventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LotteryEventService lotteryEventService;
    @MockBean
    private UserService userService;
    @MockBean
    private AdminService adminService;
    @MockBean
    private RedisService redisService;

    public String phoneNumber;
    public String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        this.phoneNumber = "010-0000-0000";

        // userService 모킹
        given(userService.verifyUser(any())).willReturn(new BaseUser(this.phoneNumber, Role.USER));

        // 엑세스 토큰 설정
        this.accessToken = getToken(this.phoneNumber);

        // 추첨 이벤트 조회
        LotteryEventResponseDto lotteryEventResponseDto = new LotteryEventResponseDto(
                LocalDateTime.of(2024, 8, 15, 0, 0, 0),
                LocalDateTime.of(2024, 8, 1, 0, 0, 0),
                LocalDateTime.of(2024, 8, 31, 0, 0, 0),
                ChronoUnit.DAYS.between(LocalDateTime.of(2024, 8, 1, 0, 0, 0), LocalDateTime.of(2024, 8, 31, 0, 0, 0))
        );
        given(lotteryEventService.getLotteryEvent()).willReturn(lotteryEventResponseDto);
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

    @Test
    @DisplayName("추첨 이벤트 조회 API 성공 테스트")
    void getLotteryEventAndServerTime() throws Exception {
        //given
        String token = this.accessToken;

        //when
        ResultActions perform = mockMvc.perform(get("/event/lottery")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.serverDateTime").value("2024-08-15T00:00:00"))
                .andExpect(jsonPath("$.eventStartDate").value("2024-08-01T00:00:00"))
                .andExpect(jsonPath("$.eventEndDate").value("2024-08-31T00:00:00"))
                .andDo(print());

    }
}