package JGS.CasperEvent.domain.event.service.redisService;

import JGS.CasperEvent.domain.event.dto.response.CasperBotResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LotteryEventRedisService {
    private static final String LIST_KEY = "recentData";
    private static final int MAX_SIZE = 100;

    private final RedisTemplate<String, CasperBotResponseDto> redisTemplate;

    @Autowired
    public LotteryEventRedisService(RedisTemplate<String, CasperBotResponseDto> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addData(CasperBotResponseDto data) {
        redisTemplate.opsForList().leftPush(LIST_KEY, data);

        Long size = redisTemplate.opsForList().size(LIST_KEY);
        if (size != null && size > MAX_SIZE) {
            redisTemplate.opsForList().trim(LIST_KEY, 0, MAX_SIZE - 1);
        }
    }

    public List<CasperBotResponseDto> getRecentData() {
        return redisTemplate.opsForList().range(LIST_KEY, 0, -1);
    }
}
