package JGS.CasperEvent.domain.event.controller.eventController;

import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class RushEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RushEventRepository rushEventRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        rushEventRepository.deleteAll(); // 기존 데이터 삭제

        // 새로운 생성자를 사용한 RushEvent 생성
        RushEvent rushEvent1 = new RushEvent("https://example.com/image1.png", "First prize");
        RushEvent rushEvent2 = new RushEvent("https://example.com/image2.png", "Second prize");

        // 엔티티 저장
        rushEventRepository.saveAll(Arrays.asList(rushEvent1, rushEvent2));
    }

    @Test
    public void getRushEventListAndServerTime() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(get("/event/rush")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.events").isArray())
                .andExpect(jsonPath("$.events.length()").value(2))
                .andExpect(jsonPath("$.events[0].prizeImageUrl").value("https://example.com/image1.png"))
                .andExpect(jsonPath("$.events[0].prizeDescription").value("First prize"))
                .andExpect(jsonPath("$.events[1].prizeImageUrl").value("https://example.com/image2.png"))
                .andExpect(jsonPath("$.events[1].prizeDescription").value("Second prize"))
                .andExpect(jsonPath("$.serverTime").exists())
                .andDo(print());
    }
}