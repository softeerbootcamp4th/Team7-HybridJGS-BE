package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto.CasperBotRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.CasperBotResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryParticipantResponseDto;
import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.repository.CasperBotRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryParticipantsRepository;
import JGS.CasperEvent.domain.event.service.redisService.RedisService;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.error.exception.CustomException;
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
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    private BaseUser user;

    private LotteryEvent lotteryEvent;
    private LotteryParticipants lotteryParticipants;
    private CasperBotRequestDto casperBotRequestDto;
    private CasperBot casperBot;
    private CasperBotResponseDto casperBotResponseDto;

    @BeforeEach
    void setUp() throws Exception {
        byte[] decodedKey = "I0EM1X1NeXKJv4Q+ifZllg==".getBytes();
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        ReflectionTestUtils.setField(lotteryEventService, "secretKey", secretKey);

        // BaseUser 엔티티
        user = new BaseUser("010-0000-0000", Role.USER);

        // 추첨 이벤트 엔티티
        lotteryEvent = new LotteryEvent(
                LocalDateTime.of(2000, 9, 27, 0, 0, 0),
                LocalDateTime.of(2100, 9, 27, 0, 0, 0),
                363
        );

        // 추첨 이벤트 참여자 엔티티
        lotteryParticipants = new LotteryParticipants(user);

        // 캐스퍼 봇 생성 요청 DTO
        casperBotRequestDto = CasperBotRequestDto.builder()
                .eyeShape(0)
                .eyePosition(0)
                .mouthShape(0)
                .color(0)
                .sticker(0)
                .name("name")
                .expectation("expectation")
                .referralId("QEszP1K8IqcapUHAVwikXA==").build();

        // 캐스퍼 봇 엔티티
        casperBot = new CasperBot(casperBotRequestDto, "010-0000-0000");

        // 캐스퍼 봇 응답 DTO
        casperBotResponseDto = CasperBotResponseDto.of(casperBot);
    }

    @Test
    @DisplayName("캐스퍼 봇 등록 테스트 - 성공")
    void postCasperBot_Success() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //given
        given(casperBotRepository.save(casperBot)).willReturn(casperBot);
        List<LotteryEvent> lotteryEventList = new ArrayList<>();
        lotteryEventList.add(lotteryEvent);
        given(lotteryEventRepository.findAll()).willReturn(lotteryEventList);

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

    @Test
    @DisplayName("응모 내역 조회 테스트 - 성공")
    void getLotteryParticipants_Success() throws UserPrincipalNotFoundException {
        //given
        given(lotteryParticipantsRepository.findByBaseUser(user))
                .willReturn(Optional.ofNullable(lotteryParticipants));
        given(casperBotRepository.findById(any())).willReturn(Optional.ofNullable(casperBot));

        //when
        LotteryParticipantResponseDto lotteryParticipantResponseDto = lotteryEventService.getLotteryParticipant(user);
        CasperBotResponseDto casperBot = lotteryParticipantResponseDto.casperBot();

        //then
        assertThat(lotteryParticipantResponseDto).isNotNull();
        assertThat(lotteryParticipantResponseDto.linkClickedCount()).isEqualTo(0);
        assertThat(lotteryParticipantResponseDto.expectations()).isEqualTo(0);
        assertThat(lotteryParticipantResponseDto.appliedCount()).isEqualTo(1);

        assertThat(lotteryParticipantResponseDto.casperBot()).isNotNull();
        assertThat(casperBot.eyeShape()).isEqualTo(0);
        assertThat(casperBot.eyePosition()).isEqualTo(0);
        assertThat(casperBot.mouthShape()).isEqualTo(0);
        assertThat(casperBot.color()).isEqualTo(0);
        assertThat(casperBot.sticker()).isEqualTo(0);
        assertThat(casperBot.name()).isEqualTo("name");
        assertThat(casperBot.expectation()).isEqualTo("expectation");
    }

    @Test
    @DisplayName("캐스퍼 봇 조회 실패 테스트")
    void getCasperBotTest_Failure() {
        //given
        given(casperBotRepository.findById(2L))
                .willThrow(new CustomException("캐스퍼 봇이 없음", CustomErrorCode.CASPERBOT_NOT_FOUND));

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                lotteryEventService.getCasperBot(2L)
        );

        //then
        assertEquals(CustomErrorCode.CASPERBOT_NOT_FOUND, exception.getErrorCode());
        assertEquals("캐스퍼 봇이 없음", exception.getMessage());
    }

}