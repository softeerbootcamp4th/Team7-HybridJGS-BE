package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.RequestDto.CasperBotRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.CasperBotResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.LotteryEventResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.LotteryParticipantResponseDto;
import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.repository.CasperBotRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryParticipantsRepository;
import JGS.CasperEvent.domain.event.service.redisService.RedisService;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.error.exception.LotteryEventNotExists;
import JGS.CasperEvent.global.jwt.repository.UserRepository;
import JGS.CasperEvent.global.util.AESUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LotteryEventService {

    private final UserRepository userRepository;
    private final LotteryEventRepository lotteryEventRepository;
    private final LotteryParticipantsRepository lotteryParticipantsRepository;
    private final CasperBotRepository casperBotRepository;
    private final RedisService redisService;
    private final SecretKey secretKey;

    public CasperBotResponseDto postCasperBot(BaseUser user, CasperBotRequestDto casperBotRequestDto) throws CustomException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        LotteryParticipants participants = registerUserIfNeed(user, casperBotRequestDto);
        LotteryEvent lotteryEvent = lotteryEventRepository.findById(1L).orElseThrow(LotteryEventNotExists::new);

        CasperBot casperBot = casperBotRepository.save(new CasperBot(casperBotRequestDto, user.getId()));
        lotteryEvent.addAppliedCount();

        participants.updateCasperId(casperBot.getCasperId());

        if (!casperBot.getExpectation().isEmpty()) {
            participants.expectationAdded();
            lotteryEvent.addAppliedCount();
        }

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


    public LotteryParticipants registerUserIfNeed(BaseUser user, CasperBotRequestDto casperBotRequestDto) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        LotteryParticipants participant = lotteryParticipantsRepository.findByBaseUser(user).orElse(null);

        if (participant == null) {
            participant = new LotteryParticipants(user);
            lotteryParticipantsRepository.save(participant);
        }

        if (!casperBotRequestDto.getReferralId().isEmpty()) {
            String referralId = AESUtils.decrypt(casperBotRequestDto.getReferralId(), secretKey);
            Optional<LotteryParticipants> referralParticipant =
                    lotteryParticipantsRepository.findByBaseUser(
                            userRepository.findById(referralId).orElse(null)
                    );
            referralParticipant.ifPresent(LotteryParticipants::linkClickedCountAdded);
        }

        user.updateLotteryParticipants(participant);
        userRepository.save(user);

        return participant;
    }

    public LotteryEventResponseDto getLotteryEvent() {
        LotteryEvent lotteryEvent = lotteryEventRepository.findById(1L).orElseThrow(LotteryEventNotExists::new);
        return LotteryEventResponseDto.of(lotteryEvent, LocalDateTime.now());
    }

}
