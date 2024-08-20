package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.RushEventResponseDto;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RushEventCacheService {

    private final RushEventRepository rushEventRepository;

    @Cacheable(value = "todayEventCache", key = "#today")
    public RushEventResponseDto getTodayEvent(LocalDate today) {
        log.info("오늘의 이벤트 캐싱 {}", today);
        // 오늘 날짜에 해당하는 모든 이벤트 꺼내옴
        List<RushEvent> rushEventList = rushEventRepository.findByEventDate(today);

        if (rushEventList.isEmpty()) {
            throw new CustomException("선착순 이벤트가 존재하지않습니다.", CustomErrorCode.NO_RUSH_EVENT);
        }

        if (rushEventList.size() > 1) {
            throw new CustomException("선착순 이벤트가 2개 이상 존재합니다.", CustomErrorCode.MULTIPLE_RUSH_EVENTS_FOUND);
        }

        return RushEventResponseDto.of(rushEventList.get(0));
    }
}
