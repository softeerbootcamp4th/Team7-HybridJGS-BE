package JGS.CasperEvent.domain.event.controller.adminController;

import JGS.CasperEvent.domain.event.dto.RequestDto.AdminRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.ImageUrlResponseDto;
import JGS.CasperEvent.domain.event.entity.admin.Admin;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AdminController.class)
//        includeFilters = {
//                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtUserFilter.class),
//                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = VerifyAdminFilter.class),
//                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class)
//})
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

    @BeforeEach
    void setUp() throws Exception {
        this.adminId = "adminId";
        this.password = "password";
        admin = new Admin(adminId, password, Role.ADMIN);
        given(adminService.verifyAdmin(any())).willReturn(admin);
        // 엑세스 토큰 설정
        this.accessToken = getToken(adminId, password);
    }


    @Test
    @DisplayName("어드민 생성 성공 테스트")
    void postAdminSuccessTest() throws Exception {
        //given
        AdminRequestDto adminRequestDto = AdminRequestDto.builder()
                .adminId(adminId)
                .password(password)
                .build();
        String requestBody = objectMapper.writeValueAsString(adminRequestDto);

        given(adminService.postAdmin(adminRequestDto)).willReturn(ResponseDto.of("관리자 생성 성공"));
        //when
        ResultActions perform = mockMvc.perform(post("/admin/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("관리자 생성 성공"))
                .andDo(print());
    }

    @Test
    @DisplayName("이미지 업로드 성공 테스트")
    void postImageSuccessTest() throws Exception {
        //given
        given(adminService.postImage(any())).willReturn(new ImageUrlResponseDto("https://image.url"));
        MockMultipartFile image = new MockMultipartFile("image", "image.png", "png", "<<data>>".getBytes());
        //when
        ResultActions perform = mockMvc.perform(multipart("/admin/image")
                .file(image)
                .header("Authorization", accessToken)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageUrl").value("https://image.url"))
                .andDo(print());
    }

    String getToken(String id, String password) throws Exception {
        String requestBody = String.format("""
                {
                    "adminId": "%s",
                    "password": "%s"
                }
                """, id, password);

        ResultActions perform = mockMvc.perform(post("/admin/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        String jsonString = perform.andReturn().getResponse().getContentAsString();
        String tokenPrefix = "\"accessToken\":\"";
        int start = jsonString.indexOf(tokenPrefix) + tokenPrefix.length();
        int end = jsonString.indexOf("\"", start);

        return "Bearer " + jsonString.substring(start, end);
    }
}