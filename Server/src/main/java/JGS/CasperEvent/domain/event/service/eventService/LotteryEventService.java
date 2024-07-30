package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.GetCasperBot;
import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import static JGS.CasperEvent.global.util.GsonUtil.getGson;

@Service
public class LotteryEventService {
    private LotteryEventRepository lotteryEventRepository;

    public GetCasperBot postCasperBot(String body) {
        Gson gson = getGson();
        CasperBot casperBot = gson.fromJson(body, CasperBot.class);
        return GetCasperBot.of(casperBot);
    }
}
