package JGS.CasperEvent.domain.event.controller.eventController;

import JGS.CasperEvent.domain.event.dto.GetCasperBot;
import JGS.CasperEvent.domain.event.service.eventService.LotteryEventService;
import JGS.CasperEvent.global.response.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event/lottery")
public class LotteryEventController {

    private final LotteryEventService lotteryEventService;

    @Autowired
    public LotteryEventController(LotteryEventService lotteryEventService) {
        this.lotteryEventService = lotteryEventService;
    }

    @PostMapping
    public CustomResponse<GetCasperBot> postCasperBot(@RequestBody String body) {
        return CustomResponse.create(lotteryEventService.postCasperBot(body));
    }
}
