package JGS.CasperEvent.domain.event.controller.eventController;

import JGS.CasperEvent.domain.event.dto.GetCasperBot;
import JGS.CasperEvent.domain.event.service.eventService.LotteryEventService;
import JGS.CasperEvent.global.response.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event/lottery")
public class LotteryEventController {

    @Autowired
    private LotteryEventService lotteryEventService;

    @PostMapping
    public ResponseEntity<CustomResponse<GetCasperBot>> postCasperBot(@CookieValue String userData,
                                                                      @RequestBody String body) {
        return new ResponseEntity<>(CustomResponse.create(lotteryEventService.postCasperBot(userData, body)), HttpStatus.CREATED);
    }

    @GetMapping("/applied")
    public ResponseEntity<CustomResponse<Boolean>> isUserApplied(@CookieValue String userData) {
        return new ResponseEntity<>(CustomResponse.success(lotteryEventService.isUserApplied(userData)), HttpStatus.OK);
    }
}
