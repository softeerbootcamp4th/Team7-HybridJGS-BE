package JGS.CasperEvent.domain.health.api;

import JGS.CasperEvent.global.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@ResponseBody
public class HealthController {

    @GetMapping
    public ResponseEntity<ResponseDto> health(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto("Server OK"));
    }
}
