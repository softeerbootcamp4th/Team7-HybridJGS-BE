package JGS.CasperEvent.domain.health.api;

import JGS.CasperEvent.global.response.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static JGS.CasperEvent.global.response.CustomResponse.response;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<CustomResponse<Boolean>> health(){
        return new ResponseEntity<>(CustomResponse.success(true), HttpStatus.OK);
    }
}
