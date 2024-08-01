package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.GetCasperBot;
import JGS.CasperEvent.domain.event.dto.GetLotteryParticipant;
import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.repository.CasperBotRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryParticipantsRepository;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.error.exception.ErrorCode;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.attribute.UserPrincipalNotFoundException;

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

        if (casperBot.getExpectation() != null) participants.expectationAdded();

        casperBotRepository.save(casperBot);
        lotteryParticipantsRepository.save(participants);
        return GetCasperBot.of(casperBot);
    }


    public GetLotteryParticipant getLotteryParticipant(String userData) throws UserPrincipalNotFoundException {
        String phoneNumber = getDecodedPhoneNumber(userData);

        LotteryParticipants participant = lotteryParticipantsRepository.findByPhoneNumber(phoneNumber).orElse(null);

        if (participant == null) throw new UserPrincipalNotFoundException("응모 내역이 없습니다.");
        else return GetLotteryParticipant.of(participant);
    }

    public GetCasperBot getCasperBot(Long casperId){
        CasperBot casperBot = casperBotRepository.findById(casperId).orElse(null);
        if(casperBot == null) throw new CustomException("캐스퍼 봇이 없음", ErrorCode.USER_NOT_FOUND);
        return GetCasperBot.of(casperBot);
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
