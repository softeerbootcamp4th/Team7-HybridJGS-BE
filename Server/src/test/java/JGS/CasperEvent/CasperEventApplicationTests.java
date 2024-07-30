package JGS.CasperEvent;

import JGS.CasperEvent.domain.health.api.HealthController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
class CasperEventApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void setup(){
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new HealthController())
				.addFilter(new CharacterEncodingFilter("UTF-8", true))
				.build();
	}


	@Test
	@DisplayName("서버 연결 테스트")
	void HealthTest() throws Exception {
		mockMvc.perform(get("/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.statusCode").value(200))
				.andExpect(jsonPath("$.result").value(true));
	}

}
