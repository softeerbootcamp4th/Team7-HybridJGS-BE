package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto.CasperBotRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.CasperBotResponseDto;
import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.repository.CasperBotRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryParticipantsRepository;
import JGS.CasperEvent.domain.event.service.redisService.RedisService;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.jwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LotteryEventServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private LotteryEventRepository lotteryEventRepository;
    @Mock
    private LotteryParticipantsRepository lotteryParticipantsRepository;
    @Mock
    private CasperBotRepository casperBotRepository;
    @Mock
    private RedisService redisService;


    @InjectMocks
    LotteryEventService lotteryEventService;

    private LotteryEvent lotteryEvent;

    @BeforeEach
    void setUp() throws Exception {
        byte[] decodedKey = "I0EM1X1NeXKJv4Q+ifZllg==".getBytes();
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        ReflectionTestUtils.setField(lotteryEventService, "secretKey", secretKey);


        // 추첨 이벤트 엔티티
        lotteryEvent = new LotteryEvent(
                LocalDateTime.of(2000, 9, 27, 0, 0, 0),
                LocalDateTime.of(2100, 9, 27, 0, 0, 0),
                363
        );

        // 추첨 이벤트 조회
        List<LotteryEvent> lotteryEventList = new ArrayList<>();
        lotteryEventList.add(lotteryEvent);
        given(lotteryEventRepository.findAll()).willReturn(lotteryEventList);
    }

    @Test
    @DisplayName("캐스퍼 봇 등록 테스트 - 성공")
    void postCasperBot_Success() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //given
        BaseUser user = new BaseUser("010-0000-0000", Role.USER);
        CasperBotRequestDto casperBotRequestDto = CasperBotRequestDto.builder()
                .eyeShape(0)
                .eyePosition(0)
                .mouthShape(0)
                .color(0)
                .sticker(0)
                .name("name")
                .expectation("expectation")
                .referralId("QEszP1K8IqcapUHAVwikXA==").build();

        CasperBot casperBot = new CasperBot(casperBotRequestDto, "010-0000-0000");

        given(casperBotRepository.save(casperBot)).willReturn(casperBot);

        //when
        CasperBotResponseDto casperBotResponseDto = lotteryEventService.postCasperBot(user, casperBotRequestDto);

        //then
        assertThat(casperBotResponseDto.eyeShape()).isEqualTo(0);
        assertThat(casperBotResponseDto.eyePosition()).isEqualTo(0);
        assertThat(casperBotResponseDto.mouthShape()).isEqualTo(0);
        assertThat(casperBotResponseDto.color()).isEqualTo(0);
        assertThat(casperBotResponseDto.sticker()).isEqualTo(0);
        assertThat(casperBotResponseDto.name()).isEqualTo("name");
        assertThat(casperBotResponseDto.expectation()).isEqualTo("expectation");
    }

}