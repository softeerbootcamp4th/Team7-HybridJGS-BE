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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private LotteryEventRedisService lotteryEventRedisService;
    @Mock
    private EventCacheService eventCacheService;


    @InjectMocks
    LotteryEventService lotteryEventService;

    private BaseUser user;

    private LotteryEvent lotteryEvent;
    private LotteryParticipants lotteryParticipants;
    private CasperBotRequestDto casperBotRequestDto;
    private CasperBot casperBot;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    @DisplayName("캐스퍼 봇 등록 테스트 - 성공")
    void postCasperBot_Success() {
        //given
        given(casperBotRepository.save(casperBot)).willReturn(casperBot);
        given(eventCacheService.getLotteryEvent()).willReturn(lotteryEvent);

        //when
        CasperBotResponseDto casperBotResponseDto = lotteryEventService.postCasperBot(user, casperBotRequestDto);

        //then
        assertThat(casperBotResponseDto.eyeShape()).isZero();
        assertThat(casperBotResponseDto.eyePosition()).isZero();
        assertThat(casperBotResponseDto.mouthShape()).isZero();
        assertThat(casperBotResponseDto.color()).isZero();
        assertThat(casperBotResponseDto.sticker()).isZero();
        assertThat(casperBotResponseDto.name()).isEqualTo("name");
        assertThat(casperBotResponseDto.expectation()).isEqualTo("expectation");
    }

    @Test
    @DisplayName("응모 내역 조회 테스트 - 성공")
    void getLotteryParticipants_Success() {
        //given
        given(lotteryParticipantsRepository.findByBaseUser(user))
                .willReturn(Optional.ofNullable(lotteryParticipants));
        //when
        LotteryEventParticipantResponseDto lotteryEventParticipantResponseDto = lotteryEventService.getLotteryParticipant(user);

        //then
        assertThat(lotteryEventParticipantResponseDto).isNotNull();
        assertThat(lotteryEventParticipantResponseDto.getLinkClickedCount()).isZero();
        assertThat(lotteryEventParticipantResponseDto.getExpectations()).isZero();
        assertThat(lotteryEventParticipantResponseDto.getAppliedCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("캐스퍼 봇 조회 테스트 - 실패")
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

    @Test
    @DisplayName("추첨 이벤트 조회 테스트 - 성공")
    void getLotteryEventTest_Success() {
        //given
        given(eventCacheService.getLotteryEvent()).willReturn(lotteryEvent);

        //when
        LotteryEventResponseDto lotteryEventResponseDto = lotteryEventService.getLotteryEvent();

        //then
        assertThat(lotteryEventResponseDto.getServerDateTime()).isNotNull();
        assertThat(lotteryEventResponseDto.getEventStartDate()).isEqualTo("2000-09-27T00:00");
        assertThat(lotteryEventResponseDto.getEventEndDate()).isEqualTo("2100-09-27T00:00");
        assertThat(lotteryEventResponseDto.getActivePeriod()).isEqualTo(36524);

    }

    @Test
    @DisplayName("추첨 이벤트 조회 테스트 - 실패 (진행중인 이벤트 없음)")
    void getLotteryEventTest_Failure_NoLotteryEvent() {
        //given
        given(eventCacheService.getLotteryEvent()).willThrow(new CustomException(CustomErrorCode.NO_LOTTERY_EVENT));

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                lotteryEventService.getLotteryEvent()
        );

        //then
        assertEquals(CustomErrorCode.NO_LOTTERY_EVENT, exception.getErrorCode());
        assertEquals("추첨 이벤트를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("추첨 이벤트 조회 테스트 - 실패(2개 이상의 이벤트 존재)")
    void getLotteryEventTest_Failure_TooManyLotteryEvent() {
        //given
        given(eventCacheService.getLotteryEvent()).willThrow(new CustomException(CustomErrorCode.TOO_MANY_LOTTERY_EVENT));

        //when
        CustomException exception = assertThrows(CustomException.class, () ->
                lotteryEventService.getLotteryEvent()
        );

        //then
        assertEquals(CustomErrorCode.TOO_MANY_LOTTERY_EVENT, exception.getErrorCode());
        assertEquals("현재 진행중인 추첨 이벤트가 2개 이상입니다.", exception.getMessage());

    }
}