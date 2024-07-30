package JGS.CasperEvent.domain.health.api;

import JGS.CasperEvent.global.response.CustomResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public CustomResponse<Boolean> health(){
        return CustomResponse.success(true);
    }
}
