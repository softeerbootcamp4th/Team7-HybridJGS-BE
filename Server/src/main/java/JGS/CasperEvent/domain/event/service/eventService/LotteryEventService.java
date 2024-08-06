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
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.jwt.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Service
@Transactional
public class LotteryEventService {

    private final UserRepository userRepository;
    private LotteryEventRepository lotteryEventRepository;
    private LotteryParticipantsRepository lotteryParticipantsRepository;
    private CasperBotRepository casperBotRepository;
    private RedisService redisService;

    @Autowired
    public LotteryEventService(LotteryEventRepository lotteryEventRepository,
                               LotteryParticipantsRepository lotteryParticipantsRepository,
                               CasperBotRepository casperBotRepository,
                               RedisService redisService, UserRepository userRepository) {
        this.lotteryEventRepository = lotteryEventRepository;
        this.lotteryParticipantsRepository = lotteryParticipantsRepository;
        this.casperBotRepository = casperBotRepository;
        this.redisService = redisService;
        this.userRepository = userRepository;
    }

    public GetCasperBot postCasperBot(BaseUser user, PostCasperBot postCasperBot) throws CustomException, BadRequestException {
        LotteryParticipants participants = registerUserIfNeed(user);

        CasperBot casperBot = new CasperBot(postCasperBot, participants.getBaseUser().getId());
        participants.updateCasperId(casperBot.getCasperId());

        if (casperBot.getExpectation() != null) participants.expectationAdded();
        lotteryParticipantsRepository.save(participants);
        casperBotRepository.save(casperBot);

        GetCasperBot casperBotDto = GetCasperBot.of(casperBot);
        redisService.addData(casperBotDto);
        return casperBotDto;
    }


    // TODO: 응모 횟수 로직 작성
    public GetLotteryParticipant getLotteryParticipant(BaseUser user) throws UserPrincipalNotFoundException {
        LotteryParticipants participant = lotteryParticipantsRepository.findByBaseUser(user)
                .orElseThrow(() -> new UserPrincipalNotFoundException("응모 내역이 없습니다."));
        return GetLotteryParticipant.of(participant, getCasperBot(participant.getCasperId()));
    }

    public GetCasperBot getCasperBot(Long casperId) {
        CasperBot casperBot = casperBotRepository.findById(casperId).orElse(null);
        if (casperBot == null) throw new CustomException("캐스퍼 봇이 없음", CustomErrorCode.CASPERBOT_NOT_FOUND);
        return GetCasperBot.of(casperBot);
    }


    public LotteryParticipants registerUserIfNeed(BaseUser user)  {
        LotteryParticipants participant = lotteryParticipantsRepository.findByBaseUser(user).orElse(null);

        if (participant == null) {
            participant = new LotteryParticipants(user);
            lotteryParticipantsRepository.save(participant);
        }

        user.updateLotteryParticipants(participant);
        userRepository.save(user);

        return participant;
    }

}
