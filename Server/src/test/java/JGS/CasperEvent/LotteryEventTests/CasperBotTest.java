package JGS.CasperEvent.LotteryEventTests;

import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;


@WebMvcTest(controllers = LotteryEvent.class)
public class CasperBotTest {
    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new LotteryEvent())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("캐스퍼 봇 생성 성공 테스트입니다.")
    void createCasperBotSuccessTest() throws Exception {
        //given
        String casperBotRequest = "{" +
                "\"eye_shape\": \"2\"," +
                "\"eye_position\": \"1\"," +
                "\"mouth_shape\": \"4\"," +
                "\"color\": \"2\"," +
                "\"sticker\": \"4\"," +
                "\"name\": \"myCasperBot\"," +
                "\"expectation\": \"myExpectation\"," + "}";

        //when
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/event/lottery")
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(casperBotRequest));

        //then
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("요청에 성공하였습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.eyeShape").value("ALLOY_WHEEL_17"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.eyePosition").value("CENTER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.mouthShape").value("BEAMING"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.color").value("SIENNA_ORANGE_METALLIC"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.sticker").value("LOVELY_RIBBON"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.name").value("myCasperBot"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.expectation").value("myExpectation"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("캐스퍼 봇 생성 실패 테스트입니다.")
    void createCasperBotFailureTest() {
        //given

        //when

        //then
    }
}
