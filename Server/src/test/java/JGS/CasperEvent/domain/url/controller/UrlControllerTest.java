package JGS.CasperEvent.domain.url.controller;

import JGS.CasperEvent.domain.event.service.adminService.AdminService;
import JGS.CasperEvent.domain.url.dto.ShortenUrlResponseDto;
import JGS.CasperEvent.domain.url.service.UrlService;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.jwt.service.UserService;
import JGS.CasperEvent.global.jwt.util.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UrlController.class)
@Import(JwtProvider.class)
class UrlControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;
    @MockBean
    private UserService userService;
    @MockBean
    private AdminService adminService;

    private BaseUser user;
    private String phoneNumber;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        phoneNumber = "010-0000-0000";
        user = new BaseUser(phoneNumber, Role.USER);
        given(userService.verifyUser(any())).willReturn(user);
        accessToken = getToken(phoneNumber);
    }

    @Test
    @DisplayName("공유 링크 생성 테스트 - 성공")
    void generateShortUrlTest_Success() throws Exception {
        //given
        ShortenUrlResponseDto shortenUrlResponseDto = new ShortenUrlResponseDto("shortenUrl1", "shortenUrl2");
        given(urlService.generateShortUrl(user)).willReturn(shortenUrlResponseDto);

        //when
        ResultActions perform = mockMvc.perform(post("/link")
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortenUrl").value("shortenUrl1"))
                .andExpect(jsonPath("$.shortenLocalUrl").value("shortenUrl2"))
                .andDo(print());
    }

    @Test
    @DisplayName("공유 링크 접속 테스트 - 성공")
    void redirectOriginalUrl_Success() throws Exception {
        //given
        String encodedId = "encodedId";
        given(urlService.getOriginalUrl(encodedId))
                .willReturn(null);

        //when
        ResultActions perform = mockMvc.perform(get("/link/" + encodedId)
                .header("Authorization", accessToken)
                .contentType(APPLICATION_JSON));

        //then
        perform.andExpect(status().isFound())
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