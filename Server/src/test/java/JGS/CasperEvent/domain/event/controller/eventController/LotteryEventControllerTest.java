package JGS.CasperEvent.domain.event.controller.eventController;

import JGS.CasperEvent.domain.event.service.eventService.LotteryEventService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LotteryEventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LotteryEventService lotteryEventService;

    @Nested
    @DisplayName("캐스퍼 봇 생성 테스트")
    class CasperBotTest {
        //TODO: Expecation이 없을때, 있을때 값 증가 테스트
        //TODO: DB에 없는 사용자 테스트 작성
        @Test
        @DisplayName("캐스퍼 봇 생성 성공 테스트")
        public void createCasperBotSuccessTest() throws Exception {
            //given
            String casperBotRequest = """
                    {
                    "eyeShape": "2",
                    "eyePosition": "1",
                    "mouthShape": "4",
                    "color": "2",
                    "sticker": "4",
                    "name": "myCasperBot",
                    "expectation": "myExpectation"
                    }""";

            Cookie myCookie = new Cookie("userData", "abc");

            //when
            ResultActions perform = mockMvc.perform(post("/event/lottery")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(casperBotRequest)
                    .cookie(myCookie));

            //then
            perform
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("생성에 성공했습니다."))
                    .andExpect(jsonPath("$.result.eyeShape").value("ALLOY_WHEEL_17"))
                    .andExpect(jsonPath("$.result.eyePosition").value("CENTER"))
                    .andExpect(jsonPath("$.result.mouthShape").value("BEAMING"))
                    .andExpect(jsonPath("$.result.color").value("SIENNA_ORANGE_METALLIC"))
                    .andExpect(jsonPath("$.result.sticker").value("LOVELY_RIBBON"))
                    .andExpect(jsonPath("$.result.name").value("myCasperBot"))
                    .andExpect(jsonPath("$.result.expectation").value("myExpectation"))
                    .andDo(print());
        }

        @Test
        @DisplayName("캐스퍼 봇 생성 실패 테스트 - 필수 필드 없음")
        void createCasperBotFailureTest_RequiredFieldNotExist() throws Exception {
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

            Cookie myCookie = new Cookie("userData", "abc");

            //when
            ResultActions perform = mockMvc.perform(post("/event/lottery")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(casperBotRequest)
                    .cookie(myCookie));

            //then
            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("eyeShape cannot be null"))
                    .andDo(print());
        }

        @Test
        @DisplayName("캐스퍼 봇 생성 실패 테스트 - 잘못된 값")
        void createCasperBotSuccessTest_WrongValue() throws Exception {
            //given
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
            Cookie myCookie = new Cookie("userData", "abc");

            //when
            ResultActions perform = mockMvc.perform(post("/event/lottery")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(casperBotRequest)
                    .cookie(myCookie));

            //then
            perform.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("eyeShape cannot be null"))
                    .andDo(print());
        }

        @Test
        @DisplayName("캐스퍼 봇 생성 실패 테스트 - 쿠키 없음")
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
            ResultActions perform = mockMvc.perform(post("/event/lottery")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(casperBotRequest));

            //then
            perform.andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("유저 정보가 없습니다."))
                    .andDo(print());

        }
    }

    @Nested
    @DisplayName("캐스퍼 봇 응모 조회")
    class CasperBotAppliedTest {
        @Test
        @DisplayName("캐스퍼 봇 응모 여부 조회 성공 - 유저가 존재할 경우")
        void userHasAppliedCasperBotSuccessTest_PresentUser() throws Exception {
            //given
            Cookie myCookie = new Cookie("userData", "abc");

            //when
            ResultActions perform = mockMvc.perform(get("/event/lottery/applied")
                    .contentType(MediaType.APPLICATION_JSON)
                    .cookie(myCookie));

            //then
            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                    .andExpect(jsonPath("$.result").exists())
                    .andDo(print());

        }

        @Test
        @DisplayName("캐스퍼 봇 응모 여부 조회 성공 - 유저가 존재하지 않는 경우")
        void userHasAppliedCasperBotSuccessTest_NotPresentUser() throws Exception {
            //given
            Cookie myCookie = new Cookie("userData", "NotPresentUser");

            //when
            ResultActions perform = mockMvc.perform(get("/event/lottery/applied")
                    .contentType(MediaType.APPLICATION_JSON)
                    .cookie(myCookie));

            //then
            perform.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("응모 내역이 없는 사용자입니다."))
                    .andDo(print());

        }

        @Test
        @DisplayName("캐스퍼 봇 응모 여부 조회 실패 - 쿠키가 없는 경우")
        void userHasAppliedCasperBotFailureTest_NotPresentCookie() throws Exception {
            //given
            //when
            ResultActions perform = mockMvc.perform(get("/event/lottery/applied")
                    .contentType(MediaType.APPLICATION_JSON));
            //then
            perform.andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("유저 정보가 없습니다."))
                    .andDo(print());

        }
    }

    @Nested
    @DisplayName("캐스퍼 봇 조회 테스트")
    class GetCasperBotTest{
        @Test
        @DisplayName("캐스퍼 봇 조회 테스트 성공")
        void GetCasperBotSuccessTest() throws Exception {
            //when
            ResultActions perform = mockMvc.perform(get("/event/lottery/caspers"));

            //then
            perform.andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                    .andExpect(jsonPath("$.result").exists())
                    .andDo(print());
        }
    }
}