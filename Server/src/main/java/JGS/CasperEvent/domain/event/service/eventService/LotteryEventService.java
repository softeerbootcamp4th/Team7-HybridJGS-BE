package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.RequestDto.PostCasperBot;
import JGS.CasperEvent.domain.event.dto.ResponseDto.GetCasperBot;
import JGS.CasperEvent.domain.event.dto.ResponseDto.GetLotteryParticipant;
import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.repository.CasperBotRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryParticipantsRepository;
import JGS.CasperEvent.domain.event.service.RedisService.RedisService;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.error.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.attribute.UserPrincipalNotFoundException;

import static JGS.CasperEvent.global.util.UserUtil.getDecodedPhoneNumber;

@Service
@Transactional
public class LotteryEventService {

    private LotteryEventRepository lotteryEventRepository;
    private LotteryParticipantsRepository lotteryParticipantsRepository;
    private CasperBotRepository casperBotRepository;
    private RedisService redisService;

    @Autowired
    public LotteryEventService(LotteryEventRepository lotteryEventRepository,
                               LotteryParticipantsRepository lotteryParticipantsRepository,
                               CasperBotRepository casperBotRepository,
                               RedisService redisService) {
        this.lotteryEventRepository = lotteryEventRepository;
        this.lotteryParticipantsRepository = lotteryParticipantsRepository;
        this.casperBotRepository = casperBotRepository;
        this.redisService = redisService;
    }

    public GetCasperBot postCasperBot(String userData, PostCasperBot postCasperBot) throws CustomException {
        LotteryParticipants participants = registerUserIfNeed(userData);

        CasperBot casperBot = new CasperBot(postCasperBot, participants.getPhoneNumber());
        casperBot.validateEnumFields();

        if (casperBot.getExpectation() != null) participants.expectationAdded();
        lotteryParticipantsRepository.save(participants);
        casperBotRepository.save(casperBot);

        GetCasperBot casperBotDto = GetCasperBot.of(casperBot);
        redisService.addData(casperBotDto);
        return casperBotDto;
    }


    // TODO: 응모 횟수 로직 작성
    public GetLotteryParticipant getLotteryParticipant(String userData) throws UserPrincipalNotFoundException {
        String phoneNumber = getDecodedPhoneNumber(userData);

        LotteryParticipants participant = lotteryParticipantsRepository.findById(phoneNumber).orElse(null);

        if (participant == null) throw new UserPrincipalNotFoundException("응모 내역이 없습니다.");
        else return GetLotteryParticipant.of(participant);
    }

    public GetCasperBot getCasperBot(Long casperId){
        CasperBot casperBot = casperBotRepository.findById(casperId).orElse(null);
        if(casperBot == null) throw new CustomException("캐스퍼 봇이 없음", CustomErrorCode.CASPERBOT_NOT_FOUND);
        return GetCasperBot.of(casperBot);
    }


    public LotteryParticipants registerUserIfNeed(String userData) {
        String phoneNumber = getDecodedPhoneNumber(userData);

        LotteryParticipants participants = lotteryParticipantsRepository.findById(phoneNumber)
                .orElse(null);

        if (participants == null) {
            participants = new LotteryParticipants(phoneNumber);
            lotteryParticipantsRepository.save(participants);
        }

        return participants;
    }

}
