package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.ResponseDto.RushEventResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.RushEventListAndServerTimeResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.RushEventRate;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.RushParticipantsRepository;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.util.RepositoryErrorHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RushEventService {
    private final RushEventRepository rushEventRepository;
    private final RushParticipantsRepository rushParticipantsRepository;

    public RushEventService(RushEventRepository rushEventRepository, RushParticipantsRepository rushParticipantsRepository) {
        this.rushEventRepository = rushEventRepository;
        this.rushParticipantsRepository = rushParticipantsRepository;
    }

    public RushEventListAndServerTimeResponseDto getAllRushEvents() {
        // DB에서 모든 RushEvent 가져오기
        List<RushEvent> rushEventList = rushEventRepository.findAll();
        // RushEvent를 DTO로 전환
        List<RushEventResponseDto> rushEventDtoList = rushEventList.stream()
                .map(RushEventResponseDto::of)
                .toList();
        // DTO 리스트와 서버 시간을 담은 RushEventListAndServerTimeResponse 객체 생성 후 반환
        return new RushEventListAndServerTimeResponseDto(rushEventDtoList, LocalDateTime.now());
    }

    public boolean isExists(Long eventId, String userId) {
        return rushParticipantsRepository.existsByRushEventIdAndUserId(eventId, userId);
    }

    public void apply(BaseUser user, Long eventId, int optionId) {
        if (isExists(eventId, user.getId())) {
            throw new CustomException("이미 응모한 회원입니다.", CustomErrorCode.CONFLICT);
        }

        // eventId 를 이용하여 rushEvent 를 꺼냄
        RushEvent rushEvent = RepositoryErrorHandler.findByIdOrElseThrow(rushEventRepository, eventId, CustomErrorCode.NO_RUSH_EVENT);

        // 새로운 RushParticipants 를 생성하여 DB 에 저장
        RushParticipants rushParticipants = new RushParticipants(user, rushEvent, optionId);
        rushParticipantsRepository.save(rushParticipants);
    }

    public RushEventRate getRushEventRate(Long eventId) {
        long leftOptionCount = rushParticipantsRepository.countByRushEventIdAndOptionId(eventId, 1);
        long rightOptionCount = rushParticipantsRepository.countByRushEventIdAndOptionId(eventId, 2);

        return new RushEventRate(leftOptionCount, rightOptionCount);
    }
}
