package JGS.CasperEvent.domain.event.controller.adminController;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("어드민 테스트")
    class AdminTest{
        @Test
        @DisplayName("어드민 생성 성공 테스트")
        void createAdminSuccessTest() throws Exception {
            //given
            String adminId = UUID.randomUUID().toString();
            String adminRequest = String.format("""
            {
                "adminId": "%s",
                "password": "password"
            }
            """, adminId);

            //when
            ResultActions perform = mockMvc.perform(post("/admin/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(adminRequest));

            //then
            perform
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("관리자 생성 성공"))
                    .andDo(print());
        }
    }
}