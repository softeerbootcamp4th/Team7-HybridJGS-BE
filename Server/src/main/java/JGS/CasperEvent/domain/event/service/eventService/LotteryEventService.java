package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.GetCasperBot;
import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.repository.CasperBotRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryParticipantsRepository;
import JGS.CasperEvent.global.error.exception.CustomException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static JGS.CasperEvent.global.util.GsonUtil.getGson;
import static JGS.CasperEvent.global.util.UserUtil.getDecodedPhoneNumber;

@Service
@Transactional
public class LotteryEventService {

    @Autowired
    private LotteryEventRepository lotteryEventRepository;
    @Autowired
    private LotteryParticipantsRepository lotteryParticipantsRepository;
    @Autowired
    private CasperBotRepository casperBotRepository;

    public GetCasperBot postCasperBot(String userData, String body) throws CustomException {
        LotteryParticipants participants = registerUserIfNeed(userData);

        Gson gson = getGson();
        CasperBot casperBot = gson.fromJson(body, CasperBot.class);
        casperBot.validateEnumFields();
        casperBot.updatePhoneNumber(participants.getPhoneNumber());

        if (casperBot.getEyePosition() != null) participants.expectationAdded();

        casperBotRepository.save(casperBot);
        lotteryParticipantsRepository.save(participants);
        return GetCasperBot.of(casperBot);
    }

    public Boolean isUserApplied(String userData) {
        String phoneNumber = getDecodedPhoneNumber(userData);

        Optional<LotteryParticipants> participant = lotteryParticipantsRepository.findByPhoneNumber(phoneNumber);
        if (participant.isEmpty()) return false;
        else return true;
    }


    public LotteryParticipants registerUserIfNeed(String userData) {
        String phoneNumber = getDecodedPhoneNumber(userData);

        LotteryParticipants participants = lotteryParticipantsRepository.findByPhoneNumber(phoneNumber)
                .orElse(null);

        if (participants == null) {
            participants = new LotteryParticipants(phoneNumber);
            lotteryParticipantsRepository.save(participants);
        }

        return participants;
    }
}
