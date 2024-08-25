package JGS.CasperEvent.domain.event.service.eventService;

import JGS.CasperEvent.domain.event.dto.response.TotalEventDateResponseDto;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final RushEventRepository rushEventRepository;
    private final LotteryEventRepository lotteryEventRepository;

    public TotalEventDateResponseDto getTotalEventDate() {
        List<LotteryEvent> lotteryEventList = lotteryEventRepository.findAll();
        List<RushEvent> rushEventList = rushEventRepository.findAll();

        if (lotteryEventList.isEmpty()) {
            throw new CustomException("추첨 이벤트가 DB에 존재하지 않습니다.", CustomErrorCode.NO_LOTTERY_EVENT);
        }

        List<LocalDate> localDateList = new ArrayList<>();

        // lotteryEvent는 어차피 최대 1개만 존재
        localDateList.add(lotteryEventList.get(0).getStartDateTime().toLocalDate());
        localDateList.add(lotteryEventList.get(0).getEndDateTime().toLocalDate());

        for (RushEvent rushEvent : rushEventList) {
            localDateList.add(rushEvent.getStartDateTime().toLocalDate());
        }

        localDateList.sort(null);

        return new TotalEventDateResponseDto(localDateList.get(0), localDateList.get(localDateList.size() - 1));
    }
}

