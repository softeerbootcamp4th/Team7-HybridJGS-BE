package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.GetRushEvent;
import JGS.CasperEvent.domain.event.dto.RushEventListAndServerTimeResponse;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.util.RepositoryErrorHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static JGS.CasperEvent.global.util.RepositoryErrorHandler.existsByIdOrElseThrow;
import static JGS.CasperEvent.global.util.RepositoryErrorHandler.findByIdOrElseThrow;

@Service
public class RushEventService {

    private final RushEventRepository rushEventRepository;

    public RushEventService(RushEventRepository rushEventRepository) {
        this.rushEventRepository = rushEventRepository;
    }

    public RushEventListAndServerTimeResponse getAllRushEvents() {
        // DB에서 모든 RushEvent 가져오기
        List<RushEvent> rushEventList = rushEventRepository.findAll();
        // RushEvent를 DTO로 전환
        List<GetRushEvent> rushEventDtoList = rushEventList.stream()
                .map(GetRushEvent::of)
                .toList();
        // DTO 리스트와 서버 시간을 담은 RushEventListAndServerTimeResponse 객체 생성 후 반환
        return new RushEventListAndServerTimeResponse(rushEventDtoList, LocalDateTime.now());
    }

//    public boolean isExists(Long eventId, String userData) {
//        RushEvent rushEvent = findByIdOrElseThrow(rushEventRepository, eventId, CustomErrorCode.NO_RUSH_EVENT);
//
//
//    }

}
