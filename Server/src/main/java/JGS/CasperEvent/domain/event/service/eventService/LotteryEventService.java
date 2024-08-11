package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.RequestDto.CasperBotRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.CasperBotResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.LotteryEventResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.LotteryParticipantResponseDto;
import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.repository.CasperBotRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryParticipantsRepository;
import JGS.CasperEvent.domain.event.service.redisService.RedisService;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
public class LotteryEventService {

    private final UserRepository userRepository;
    private final LotteryEventRepository lotteryEventRepository;
    private final LotteryParticipantsRepository lotteryParticipantsRepository;
    private final CasperBotRepository casperBotRepository;
    private final RedisService redisService;

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

    public CasperBotResponseDto postCasperBot(BaseUser user, CasperBotRequestDto casperBotRequestDto) throws CustomException {
        LotteryParticipants participants = registerUserIfNeed(user);

        CasperBot casperBot = casperBotRepository.save(new CasperBot(casperBotRequestDto, user.getId()));

        participants.updateCasperId(casperBot.getCasperId());

        if (!casperBot.getExpectation().isEmpty()) participants.expectationAdded();

        CasperBotResponseDto casperBotDto = CasperBotResponseDto.of(casperBot);
        redisService.addData(casperBotDto);

        return casperBotDto;
    }

    public LotteryParticipantResponseDto getLotteryParticipant(BaseUser user) throws UserPrincipalNotFoundException {
        LotteryParticipants participant = lotteryParticipantsRepository.findByBaseUser(user)
                .orElseThrow(() -> new UserPrincipalNotFoundException("응모 내역이 없습니다."));
        return LotteryParticipantResponseDto.of(participant, getCasperBot(participant.getCasperId()));
    }

    public CasperBotResponseDto getCasperBot(Long casperId) {
        CasperBot casperBot = casperBotRepository.findById(casperId).orElse(null);
        if (casperBot == null) throw new CustomException("캐스퍼 봇이 없음", CustomErrorCode.CASPERBOT_NOT_FOUND);
        return CasperBotResponseDto.of(casperBot);
    }


    public LotteryParticipants registerUserIfNeed(BaseUser user) {
        LotteryParticipants participant = lotteryParticipantsRepository.findByBaseUser(user).orElse(null);

        if (participant == null) {
            participant = new LotteryParticipants(user);
            lotteryParticipantsRepository.save(participant);
        }

        user.updateLotteryParticipants(participant);
        userRepository.save(user);

        return participant;
    }

    // TODO: 가짜 API, DB 접속되도록 수정
    public LotteryEventResponseDto getLotteryEvent() {
        return new LotteryEventResponseDto(LocalDateTime.now(),
                LocalDate.of(2000, 9, 27).atStartOfDay(),
                LocalDate.of(2100, 9, 27).atStartOfDay(),
                ChronoUnit.DAYS.between(LocalDate.of(2000, 9, 27), LocalDate.of(2100, 9, 27)));
    }

}
