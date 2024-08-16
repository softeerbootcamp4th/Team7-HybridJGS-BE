package JGS.CasperEvent.domain.event.controller.adminController;

import JGS.CasperEvent.domain.event.dto.RequestDto.AdminRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto.LotteryEventRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.ImageUrlResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventDetailResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventResponseDto;
import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.service.adminService.AdminService;
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

import static org.mockito.ArgumentMatchers.any;
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

    private LotteryEvent lotteryEvent;
    private LotteryEventRequestDto lotteryEventRequestDto;
    private LotteryEventResponseDto lotteryEventResponseDto;

    @BeforeEach
    void setUp() throws Exception {
        this.adminId = "adminId";
        this.password = "password";
        admin = new Admin(adminId, password, Role.ADMIN);
        given(adminService.verifyAdmin(any())).willReturn(admin);
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
        ResultActions perform = mockMvc.perform(multipart("/admin/image").file(image).header("Authorization", accessToken).contentType(MediaType.MULTIPART_FORM_DATA));

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
        perform.andExpect(status().isOk()).andExpect(jsonPath("$.startDate").value("2000-09-27")).andExpect(jsonPath("$.startTime").value("00:00:00")).andExpect(jsonPath("$.endDate").value("2100-09-27")).andExpect(jsonPath("$.endTime").value("00:00:00")).andExpect(jsonPath("$.appliedCount").value(0)).andExpect(jsonPath("$.winnerCount").value(315)).andExpect(jsonPath("$.status").value("DURING")).andDo(print());
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