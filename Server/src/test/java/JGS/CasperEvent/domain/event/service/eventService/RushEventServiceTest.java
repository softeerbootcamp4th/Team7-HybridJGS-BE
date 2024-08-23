package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.response.rush.RushEventListResponseDto;
import JGS.CasperEvent.domain.event.dto.response.rush.RushEventOptionResponseDto;
import JGS.CasperEvent.domain.event.dto.response.rush.RushEventResponseDto;
import JGS.CasperEvent.domain.event.dto.response.rush.RushEventResultResponseDto;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.entity.event.RushOption;
import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushOptionRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.RushParticipantsRepository;
import JGS.CasperEvent.domain.event.service.redisService.RushEventRedisService;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.enums.Position;
import JGS.CasperEvent.global.error.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class RushEventServiceTest {

    @Mock
    private RushEventRepository rushEventRepository;
    @Mock
    private RushParticipantsRepository rushParticipantsRepository;
    @Mock
    private RushOptionRepository rushOptionRepository;
    @Mock
    private EventCacheService eventCacheService;
    @Mock
    private RushEventRedisService rushEventRedisService;


    @InjectMocks
    RushEventService rushEventService;

    private RushEvent rushEvent;
    private RushEventResponseDto todayEvent;


    @BeforeEach
    void setUp() {
        rushEvent = spy(new RushEvent(LocalDateTime.now(), LocalDateTime.now().plusDays(1), 315, "image-url", "prize-description"));
        lenient().doReturn(1L).when(rushEvent).getRushEventId();
        RushOption leftOption = new RushOption(rushEvent, "leftMainText", "leftSubText", "resultMainText", "resultSubText", "leftImageUrl", Position.LEFT);
        RushOption rightOption = new RushOption(rushEvent, "rightMainText", "rightSubText", "resultMainText", "resultSubText", "rightImageUrl", Position.RIGHT);

        rushEvent.addOption(leftOption, rightOption);

        todayEvent = RushEventResponseDto.of(rushEvent);
    }

    @Test
    @DisplayName("모든 RushEvent 조회")
    void getAllRushEvents() {
        // given
        List<RushEventResponseDto> rushEventList = List.of(
                todayEvent, todayEvent
        );

        given(eventCacheService.getTodayEvent(LocalDate.now())).willReturn(todayEvent);
        given(eventCacheService.getAllRushEvent()).willReturn(rushEventList);

        // when
        RushEventListResponseDto allRushEvents = rushEventService.getAllRushEvents();

        // then
        assertNotNull(allRushEvents);
        assertEquals(2, allRushEvents.getEvents().size());
        assertEquals(1, allRushEvents.getTodayEventId());
    }

    @Test
    @DisplayName("해당 유저가 선착순 이벤트 응모했는지 여부 테스트")
    void isExists() {
        // given
        BaseUser user = new BaseUser();

        given(eventCacheService.getTodayEvent(LocalDate.now())).willReturn(todayEvent);
        given(rushParticipantsRepository.existsByRushEvent_RushEventIdAndBaseUser_PhoneNumber(1L, user.getPhoneNumber())).willReturn(true);

        // when
        boolean exists = rushEventService.isExists(user.getPhoneNumber());

        // then
        assertTrue(exists);
    }

    @Test
    @DisplayName("선착순 이벤트 응모 테스트")
    void apply() {
        // given
        BaseUser user = new BaseUser();

        given(eventCacheService.getTodayEvent(LocalDate.now())).willReturn(todayEvent);
        given(rushParticipantsRepository.existsByRushEvent_RushEventIdAndBaseUser_PhoneNumber(1L, user.getPhoneNumber())).willReturn(false);
        RushEvent mockRushEvent = new RushEvent();
        given(rushEventRepository.findById(1L)).willReturn(Optional.of(mockRushEvent));

        // when
        rushEventService.apply(user, 1);

        // then
        verify(rushParticipantsRepository).save(any(RushParticipants.class));
    }

    @Test
    @DisplayName("선착순 이벤트 응모 테스트 (이미 응모한 유저인 경우)")
    void apply2() {
        // given
        BaseUser user = spy(new BaseUser());
        given(user.getPhoneNumber()).willReturn("010-0000-0000");

        given(eventCacheService.getTodayEvent(LocalDate.now())).willReturn(todayEvent);
        given(rushParticipantsRepository.existsByRushEvent_RushEventIdAndBaseUser_PhoneNumber(1L, "010-0000-0000")).willReturn(true);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                rushEventService.apply(user, 1)
        );

        assertEquals(CustomErrorCode.CONFLICT, exception.getErrorCode());
        assertEquals("이미 응모한 회원입니다.", exception.getMessage());
    }


    @Test
    @DisplayName("선착순 이벤트 비율 조회 테스트")
    void getRushEventRate() {
        // given
        BaseUser user = new BaseUser();

        given(eventCacheService.getTodayEvent(LocalDate.now())).willReturn(todayEvent);
        given(rushParticipantsRepository.getOptionIdByUserId(user.getPhoneNumber())).willReturn(Optional.of(1));
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1)).willReturn(100L);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 2)).willReturn(200L);

        // when
        RushEventResultResponseDto result = rushEventService.getRushEventRate(user);

        // then
        assertNotNull(result);
        assertEquals(1, result.getOptionId());
        assertEquals(100, result.getLeftOption());
        assertEquals(200, result.getRightOption());
    }

    @Test
    @DisplayName("선착순 이벤트 결과 조회 테스트 (동점이 아니고 당첨인 경우)")
    void getRushEventResult() {
        // given
        BaseUser user = new BaseUser();

        given(eventCacheService.getTodayEvent(LocalDate.now())).willReturn(todayEvent);
        given(rushParticipantsRepository.getOptionIdByUserId(user.getPhoneNumber())).willReturn(Optional.of(1));
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1)).willReturn(700L);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 2)).willReturn(500L);
        given(rushParticipantsRepository.findUserRankByEventIdAndUserIdAndOptionId(1L, user.getPhoneNumber(), 1)).willReturn(300L);

        // when
        RushEventResultResponseDto result = rushEventService.getRushEventResult(user);

        // then
        assertNotNull(result);
        assertEquals(1, result.getOptionId());
        assertEquals(700, result.getLeftOption());
        assertEquals(500, result.getRightOption());
        assertEquals(700, result.getTotalParticipants());
        assertEquals(300, result.getRank());
        assertTrue(result.getIsWinner());
    }

    @Test
    @DisplayName("선착순 이벤트 결과 조회 테스트 (동점이 아니고 내가 선택한 옵션이 진 경우)")
    void getRushEventResult2() {
        // given
        BaseUser user = new BaseUser();

        given(eventCacheService.getTodayEvent(LocalDate.now())).willReturn(todayEvent);
        given(rushParticipantsRepository.getOptionIdByUserId(user.getPhoneNumber())).willReturn(Optional.of(2));
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1)).willReturn(700L);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 2)).willReturn(500L);
        given(rushParticipantsRepository.findUserRankByEventIdAndUserIdAndOptionId(1L, user.getPhoneNumber(), 2)).willReturn(300L);

        // when
        RushEventResultResponseDto result = rushEventService.getRushEventResult(user);

        // then
        assertNotNull(result);
        assertEquals(2, result.getOptionId());
        assertEquals(700, result.getLeftOption());
        assertEquals(500, result.getRightOption());
        assertEquals(500, result.getTotalParticipants());
        assertEquals(300, result.getRank());
        assertFalse(result.getIsWinner());
    }

    @Test
    @DisplayName("선착순 이벤트 결과 조회 테스트 (동점이 아니고 내가 선택한 옵션이 이겼는데 등수에 미치지 못한 경우)")
    void getRushEventResult3() {
        // given
        BaseUser user = new BaseUser();

        given(eventCacheService.getTodayEvent(LocalDate.now())).willReturn(todayEvent);
        given(rushParticipantsRepository.getOptionIdByUserId(user.getPhoneNumber())).willReturn(Optional.of(1));
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1)).willReturn(700L);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 2)).willReturn(500L);
        given(rushParticipantsRepository.findUserRankByEventIdAndUserIdAndOptionId(1L, user.getPhoneNumber(), 1)).willReturn(400L);

        // when
        RushEventResultResponseDto result = rushEventService.getRushEventResult(user);

        // then
        assertNotNull(result);
        assertEquals(1, result.getOptionId());
        assertEquals(700, result.getLeftOption());
        assertEquals(500, result.getRightOption());
        assertEquals(700, result.getTotalParticipants());
        assertEquals(400, result.getRank());
        assertFalse(result.getIsWinner());
    }

    @Test
    @DisplayName("선착순 이벤트 결과 조회 테스트 (동점인데 당첨된 경우)")
    void getRushEventResult4() {
        // given
        BaseUser user = new BaseUser();

        given(eventCacheService.getTodayEvent(LocalDate.now())).willReturn(todayEvent);
        given(rushParticipantsRepository.getOptionIdByUserId(user.getPhoneNumber())).willReturn(Optional.of(1));
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1)).willReturn(500L);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 2)).willReturn(500L);
        given(rushParticipantsRepository.findUserRankByEventIdAndUserId(1L, user.getPhoneNumber())).willReturn(300L);
        // when
        RushEventResultResponseDto result = rushEventService.getRushEventResult(user);

        // then
        assertNotNull(result);
        assertEquals(1, result.getOptionId());
        assertEquals(500, result.getLeftOption());
        assertEquals(500, result.getRightOption());
        assertEquals(1000, result.getTotalParticipants());
        assertEquals(300, result.getRank());
        assertTrue(result.getIsWinner());
    }

    @Test
    @DisplayName("선착순 이벤트 결과 조회 테스트 (동점인데 등수에 미치지 못한 경우)")
    void getRushEventResult5() {
        // given
        BaseUser user = new BaseUser();

        given(eventCacheService.getTodayEvent(LocalDate.now())).willReturn(todayEvent);
        given(rushParticipantsRepository.getOptionIdByUserId(user.getPhoneNumber())).willReturn(Optional.of(1));
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1)).willReturn(500L);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 2)).willReturn(500L);
        given(rushParticipantsRepository.findUserRankByEventIdAndUserId(1L, user.getPhoneNumber())).willReturn(400L);
        // when
        RushEventResultResponseDto result = rushEventService.getRushEventResult(user);

        // then
        assertNotNull(result);
        assertEquals(1, result.getOptionId());
        assertEquals(500, result.getLeftOption());
        assertEquals(500, result.getRightOption());
        assertEquals(1000, result.getTotalParticipants());
        assertEquals(400, result.getRank());
        assertFalse(result.getIsWinner());
    }

    @Test
    @DisplayName("선착순 이벤트 결과 조회 테스트 (응모하지 않았는데 결과 조회창으로 넘어가서 호출되는 경우)")
    void getRushEventResult6() {
        // given
        BaseUser user = new BaseUser();

        given(eventCacheService.getTodayEvent(LocalDate.now())).willReturn(todayEvent);
        given(rushParticipantsRepository.getOptionIdByUserId(user.getPhoneNumber())).willReturn(Optional.empty());
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 1)).willReturn(500L);
        given(rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(1L, 2)).willReturn(500L);

        // when & then
        RushEventResultResponseDto result = rushEventService.getRushEventResult(user);

        // then
        assertNotNull(result);
        assertNull(result.getOptionId());
        assertEquals(500, result.getLeftOption());
        assertEquals(500, result.getRightOption());
        assertNull(result.getTotalParticipants());
        assertNull(result.getRank());
        assertNull(result.getIsWinner());
    }

    @Test
    @DisplayName("선착순 이벤트 테스트 API 테스트")
    void setTodayEventToRedis() {
        // given
        RushEvent mockRushEvent = new RushEvent();
        RushOption rushOption = new RushOption();
        given(rushEventRepository.save(any(RushEvent.class))).willReturn(mockRushEvent);
        given(rushOptionRepository.save(any(RushOption.class))).willReturn(rushOption);

        // when
        rushEventService.setRushEvents();

        // then
        verify(rushParticipantsRepository).deleteAllInBatch();
        verify(rushOptionRepository).deleteAllInBatch();
        verify(rushEventRepository).deleteAllInBatch();
    }

    @Test
    @DisplayName("오늘의 선착순 이벤트의 선택지 조회 테스트")
    void getTodayRushEventOptions() {
        // given
        RushEvent spyRushEvent = spy(new RushEvent(LocalDateTime.now(), LocalDateTime.now().plusDays(1), 315, "image-url", "prize-description"));
        given(spyRushEvent.getRushEventId()).willReturn(1L);
        RushOption leftOption = new RushOption(spyRushEvent, "leftMainText", "leftSubText", "resultMainText", "resultSubText", "leftImageUrl", Position.LEFT);
        RushOption rightOption = new RushOption(spyRushEvent, "rightMainText", "rightSubText", "resultMainText", "resultSubText", "rightImageUrl", Position.RIGHT);

        spyRushEvent.addOption(leftOption, rightOption);

        RushEventResponseDto todayRushEvent = RushEventResponseDto.of(spyRushEvent);

        given(eventCacheService.getTodayEvent(LocalDate.now())).willReturn(todayRushEvent);

        // when
        RushEventResponseDto result = rushEventService.getTodayRushEventOptions();

        // then
        assertNotNull(result);
        assertEquals("leftMainText", result.getLeftOption().getMainText());
        assertEquals("rightMainText", result.getRightOption().getMainText());
    }

    @Test
    @DisplayName("선택지 결과 조회 (옵션 개수가 2개인 경우)")
    void getRushEventOptionResult() {
        // given
        int optionId = 1;
        given(eventCacheService.getTodayEvent(LocalDate.now())).willReturn(todayEvent);
        // when
        RushEventOptionResponseDto result = rushEventService.getRushEventOptionResult(optionId);

        // then
        assertNotNull(result);
        assertEquals("leftMainText", result.getMainText());
    }

    @Test
    @DisplayName("선택지 결과 조회 (옵션 개수가 3개 이상인 경우)")
    void getRushEventOptionResult2() {
        // given
        int optionId = 1;

        RushEventResponseDto todayRushEvent = RushEventResponseDto.of(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                315,
                "image-url",
                "prize-description",
                Set.of(
                        RushEventOptionResponseDto.of(1L, "leftMainText", "leftSubText", "resultMainText", "resultSubText", "leftImageUrl", Position.LEFT, LocalDateTime.now(), LocalDateTime.now()),
                        RushEventOptionResponseDto.of(2L, "rightMainText", "rightSubText", "resultMainText", "resultSubText", "rightImageUrl", Position.RIGHT, LocalDateTime.now(), LocalDateTime.now()),
                        RushEventOptionResponseDto.of(3L, "rightMainText", "rightSubText", "resultMainText", "resultSubText", "rightImageUrl", Position.RIGHT, LocalDateTime.now(), LocalDateTime.now())
                )
        );


        given(eventCacheService.getTodayEvent(LocalDate.now())).willReturn(todayRushEvent);
        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                rushEventService.getRushEventOptionResult(optionId)
        );

        assertEquals(CustomErrorCode.INVALID_RUSH_EVENT_OPTIONS_COUNT, exception.getErrorCode());
        assertEquals("해당 이벤트의 선택지가 2개가 아닙니다.", exception.getMessage());
    }

    @Test
    @DisplayName("선택지 결과 조회 (사용자가 선택한 선택지가 1 또는 2가 아닌 경우)")
    void getRushEventOptionResult3() {
        // given
        int optionId = 3;

        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                rushEventService.getRushEventOptionResult(optionId)
        );

        assertEquals(CustomErrorCode.INVALID_RUSH_EVENT_OPTION_ID, exception.getErrorCode());
        assertEquals("optionId는 1 또는 2여야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("선택지 결과 조회 (사용자가 선택한 옵션이 DB에 없는 경우)")
    void getRushEventOptionResult4() {
        // given
        int optionId = 1;

        RushEventResponseDto todayRushEvent = RushEventResponseDto.of(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                315,
                "image-url",
                "prize-description",
                Set.of(
                        RushEventOptionResponseDto.of(2L, "leftMainText", "leftSubText", "resultMainText", "resultSubText", "leftImageUrl", Position.RIGHT, LocalDateTime.now(), LocalDateTime.now()),
                        RushEventOptionResponseDto.of(3L, "rightMainText", "rightSubText", "resultMainText", "resultSubText", "rightImageUrl", Position.RIGHT, LocalDateTime.now(), LocalDateTime.now())
                )
        );

        given(eventCacheService.getTodayEvent(LocalDate.now())).willReturn(todayRushEvent);
        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                rushEventService.getRushEventOptionResult(optionId)
        );

        assertEquals(CustomErrorCode.NO_RUSH_EVENT_OPTION, exception.getErrorCode());
        assertEquals("사용자가 선택한 선택지가 존재하지 않습니다.", exception.getMessage());
    }
}
