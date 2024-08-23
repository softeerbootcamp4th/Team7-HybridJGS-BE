package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.request.lotteryEventDto.CasperBotRequestDto;
import JGS.CasperEvent.domain.event.dto.response.lottery.CasperBotResponseDto;
import JGS.CasperEvent.domain.event.dto.response.lottery.LotteryEventParticipantResponseDto;
import JGS.CasperEvent.domain.event.dto.response.lottery.LotteryEventResponseDto;
import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.repository.CasperBotRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryParticipantsRepository;
import JGS.CasperEvent.domain.event.service.redisService.LotteryEventRedisService;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.jwt.repository.UserRepository;
import JGS.CasperEvent.global.util.AESUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LotteryEventService {

    private static final Logger log = LoggerFactory.getLogger(LotteryEventService.class);
    private final UserRepository userRepository;
    private final LotteryParticipantsRepository lotteryParticipantsRepository;
    private final CasperBotRepository casperBotRepository;
    private final LotteryEventRedisService lotteryEventRedisService;
    private final SecretKey secretKey;
    private final EventCacheService eventCacheService;
    private final LotteryEventRepository lotteryEventRepository;

    public CasperBotResponseDto postCasperBot(BaseUser user, CasperBotRequestDto casperBotRequestDto) throws CustomException {
        LotteryEvent lotteryEvent = eventCacheService.getLotteryEvent();
        LotteryParticipants participants = registerUserIfNeed(lotteryEvent, user, casperBotRequestDto);

        CasperBot casperBot = casperBotRepository.save(new CasperBot(casperBotRequestDto, user.getPhoneNumber()));
        lotteryEvent.addAppliedCount();

        participants.updateCasperId(casperBot.getCasperId());

        if (!casperBot.getExpectation().isEmpty()) {
            participants.expectationAdded();
        }

        CasperBotResponseDto casperBotDto = CasperBotResponseDto.of(casperBot);
        lotteryEventRedisService.addData(casperBotDto);
        eventCacheService.setLotteryEvent();
        lotteryEventRepository.save(lotteryEvent);

        return casperBotDto;
    }

    public LotteryEventParticipantResponseDto getLotteryParticipant(BaseUser user) {
        LotteryParticipants participant = lotteryParticipantsRepository.findByBaseUser(user)
                .orElseThrow(() -> new CustomException("응모 내역이 없습니다.", CustomErrorCode.USER_NOT_FOUND));
        return LotteryEventParticipantResponseDto.of(participant);
    }

    public CasperBotResponseDto getCasperBot(Long casperId) {
        CasperBot casperBot = casperBotRepository.findById(casperId).orElse(null);
        if (casperBot == null) throw new CustomException("캐스퍼 봇이 없음", CustomErrorCode.CASPERBOT_NOT_FOUND);
        return CasperBotResponseDto.of(casperBot);
    }


    public LotteryParticipants registerUserIfNeed(LotteryEvent lotteryEvent, BaseUser user, CasperBotRequestDto casperBotRequestDto) {
        LotteryParticipants participant = lotteryParticipantsRepository.findByBaseUser(user).orElse(null);

        if (participant == null) {
            participant = new LotteryParticipants(user);
            lotteryParticipantsRepository.save(participant);

            addReferralAppliedCount(casperBotRequestDto);
        } else lotteryEvent.addAppliedCount();

        return participant;
    }

    private void addReferralAppliedCount(CasperBotRequestDto casperBotRequestDto) {
        String encryptedReferralId = casperBotRequestDto.getReferralId();
        if (encryptedReferralId == null) return;
        try {
            String referralId = AESUtils.decrypt(casperBotRequestDto.getReferralId(), secretKey);
            Optional<LotteryParticipants> referralParticipant =
                    lotteryParticipantsRepository.findByBaseUser(
                            userRepository.findByPhoneNumber(referralId).orElse(null)
                    );
            referralParticipant.ifPresent(LotteryParticipants::linkClickedCountAdded);
        } catch (Exception e) {
            log.debug(e.getLocalizedMessage());
        }

    }

    public LotteryEventResponseDto getLotteryEvent() {
        LotteryEvent lotteryEvent = eventCacheService.getLotteryEvent();
        return LotteryEventResponseDto.of(lotteryEvent, LocalDateTime.now());
    }
}
