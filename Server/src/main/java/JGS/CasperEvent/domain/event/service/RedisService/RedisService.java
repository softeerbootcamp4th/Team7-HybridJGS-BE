package JGS.CasperEvent.domain.event.service.RedisService;

import JGS.CasperEvent.domain.event.dto.ResponseDto.GetCasperBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisService {
    private static final String LIST_KEY = "recentData";
    private static final int MAX_SIZE = 100;

    private final RedisTemplate<String, GetCasperBot> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, GetCasperBot> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addData(GetCasperBot data) {
        redisTemplate.opsForList().leftPush(LIST_KEY, data);

        Long size = redisTemplate.opsForList().size(LIST_KEY);
        if (size != null && size > MAX_SIZE) {
            redisTemplate.opsForList().trim(LIST_KEY, 0, MAX_SIZE - 1);
        }
    }

    public List<GetCasperBot> getRecentData() {
        return redisTemplate.opsForList().range(LIST_KEY, 0, -1);
    }
}
