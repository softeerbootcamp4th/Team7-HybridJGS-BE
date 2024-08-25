package JGS.CasperEvent.domain.event.service.redisService;

import JGS.CasperEvent.domain.event.repository.participantsRepository.RushParticipantsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RushEventRedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final RushParticipantsRepository rushParticipantsRepository;

    public Long getOptionCount(Long eventId, int optionId) {
        String redisKey = getRedisKey(eventId, optionId);

        // Redis에서 값 조회
        String cachedCount = redisTemplate.opsForValue().get(redisKey);

        if (cachedCount != null) {
            // Redis에 값이 있으면 캐시된 값 반환
            return Long.valueOf(cachedCount);
        } else {
            // Redis에 값이 없으면 DB에서 값 조회
            long countFromDb = rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(eventId, optionId);

            // 조회한 값을 Redis에 저장
            redisTemplate.opsForValue().set(redisKey, String.valueOf(countFromDb));

            return countFromDb;
        }
    }

    public void incrementOptionCount(Long eventId, int optionId) {
        String redisKey = getRedisKey(eventId, optionId);

        // Redis에서 해당 키의 값을 증가시킴
        redisTemplate.opsForValue().increment(redisKey);
    }

    private String getRedisKey(Long eventId, int optionId) {
        return "rushEvent:" + eventId + ":option:" + optionId;
    }

    public void clearAllrushEventRate() {
        // 특정 rushEventId에 대한 모든 옵션 키들을 가져와 삭제
        String pattern = "rushEvent:*" + ":option:*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
