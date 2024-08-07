package JGS.CasperEvent.domain.event.controller.eventController;

import jakarta.servlet.http.Cookie;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class LotteryEventControllerTest {
    @Autowired
    private MockMvc mockMvc;

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


    @Nested
    @DisplayName("캐스퍼 봇 생성 테스트")
    class CasperBotTest {
        //TODO: Expecation이 없을때, 있을때 값 증가 테스트
        //TODO: DB에 없는 사용자 테스트 작성
        @Test
        @DisplayName("캐스퍼 봇 생성 성공 테스트")
        public void createCasperBotSuccessTest() throws Exception {
            //given
            String accessToken = getToken("010-0000-0000");

            String casperBotRequest = """
                    {
                    "eyeShape": "2",
                    "eyePosition": "1",
                    "mouthShape": "4",
                    "color": "2",
                    "sticker": "4",
                    "name": "myCasperBot",
                    "expectation": "myExpectation"
                    }
                    """;

            //when
            ResultActions perform = mockMvc.perform(post("/event/lottery/casperBot")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(casperBotRequest)
                    .header("Authorization", accessToken));

            //then
            perform
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.eyeShape").value(2))
                    .andExpect(jsonPath("$.eyePosition").value(1))
                    .andExpect(jsonPath("$.mouthShape").value(4))
                    .andExpect(jsonPath("$.color").value(2))
                    .andExpect(jsonPath("$.sticker").value(4))
                    .andExpect(jsonPath("$.name").value("myCasperBot"))
                    .andExpect(jsonPath("$.expectation").value("myExpectation"))
                    .andDo(print());
        }

        @Test
        @DisplayName("캐스퍼 봇 생성 실패 테스트 - 필수 필드 없음")
        void createCasperBotFailureTest_RequiredFieldNotExist() throws Exception {
            String accessToken = getToken("010-0000-0000");
            //given
            String casperBotRequest = """
                    {
                    "eye_shape": "2",
                    "eye_position": "1",
                    "mouth_shape": "4",
                    "color": "2",
                    "sticker": "4",
                    "expectation": "myExpectation"
                    }
                    """;


            //when
            ResultActions perform = mockMvc.perform(post("/event/lottery/casperBot")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(casperBotRequest)
                    .header("Authorization", accessToken));

            //then
            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("INVALID_CASPERBOT_PARAMETER"))
                    .andDo(print());

        }

        @Test
        @DisplayName("캐스퍼 봇 생성 실패 테스트 - 잘못된 값")
        void createCasperBotSuccessTest_WrongValue() throws Exception {
            //given
            String accessToken = getToken("010-0000-0000");
            String casperBotRequest = """
                    {
                    "eyeShape": "15",
                    "eyePosition": "1",
                    "mouthShape": "4",
                    "color": "2",
                    "sticker": "4",
                    "name": "myCasperBot",
                    "expectation": "myExpectation"
                    }""";

            //when
            ResultActions perform = mockMvc.perform(post("/event/lottery/casperBot")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(casperBotRequest)
                    .header("Authorization", accessToken));

            //then
            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("INVALID_CASPERBOT_PARAMETER"))
                    .andDo(print());
        }

        @Test
        @DisplayName("캐스퍼 봇 생성 실패 테스트 - 인증 토큰 없음")
        void createCasperBotSuccessTest_CookieNotPresent() throws Exception {
            //given
            String casperBotRequest = """
                    {
                    "eyeShape": "1",
                    "eyePosition": "1",
                    "mouthShape": "4",
                    "color": "2",
                    "sticker": "4",
                    "name": "myCasperBot",
                    "expectation": "myExpectation"
                    }
                    """;

            //when
            ResultActions perform = mockMvc.perform(post("/event/lottery/casperBot")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(casperBotRequest));

            //then
            perform.andExpect(status().isUnauthorized())
                    .andDo(print());

        }
    }

    @Nested
    @DisplayName("캐스퍼 봇 응모 조회")
    class CasperBotAppliedTest {
        @Test
        @DisplayName("캐스퍼 봇 응모 여부 조회 성공 - 유저가 존재할 경우")
        void userHasAppliedCasperBotSuccessTest_PresentUser() throws Exception {
            String accessToken = getToken("010-0000-0000");

            String casperBotRequest = """
                    {
                    "eyeShape": "2",
                    "eyePosition": "1",
                    "mouthShape": "4",
                    "color": "2",
                    "sticker": "4",
                    "name": "myCasperBot",
                    "expectation": "myExpectation"
                    }
                    """;
            //when
            mockMvc.perform(post("/event/lottery/casperBot")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(casperBotRequest)
                    .header("Authorization", accessToken));

            //when
            ResultActions perform = mockMvc.perform(get("/event/lottery/applied")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", accessToken));

            //then
            perform.andExpect(status().isOk())
                    .andDo(print());

        }

        @Test
        @DisplayName("캐스퍼 봇 응모 여부 조회 성공 - 유저가 존재하지 않는 경우")
        void userHasAppliedCasperBotSuccessTest_NotPresentUser() throws Exception {
            //given
            String accessToken = getToken("010-1234-1234");

            //when
            ResultActions perform = mockMvc.perform(get("/event/lottery/applied")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", accessToken));
            //then
            perform.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("응모하지 않은 사용자입니다."))
                    .andDo(print());

        }

        @Test
        @DisplayName("캐스퍼 봇 응모 여부 조회 실패 - 토큰이 없는 경우")
        void userHasAppliedCasperBotFailureTest_NotPresentCookie() throws Exception {
            //when
            ResultActions perform = mockMvc.perform(get("/event/lottery/applied")
                    .contentType(MediaType.APPLICATION_JSON));
            //then
            perform.andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errorCode").value("JWT_MISSING"))
                    .andExpect(jsonPath("$.message").value("인증 토큰이 존재하지 않습니다."))
                    .andDo(print());

        }
    }

    @Nested
    @DisplayName("캐스퍼 봇 조회 테스트")
    class GetCasperBotTest {
        @Test
        @DisplayName("캐스퍼 봇 조회 테스트 성공 - Redis")
        void GetCasperBotSuccessTest_redis() throws Exception {
            for (int i = 0; i < 100; i++) {
                ResultActions perform = mockMvc.perform(get("/event/lottery/caspers"));

                //then
                perform.andExpect(status().isOk())
                        .andExpect(result -> {
                            String responseBody = result.getResponse().getContentAsString();
                            assertFalse(responseBody.isEmpty(), "Response body should not be empty");
                        });

            }
        }
    }
}