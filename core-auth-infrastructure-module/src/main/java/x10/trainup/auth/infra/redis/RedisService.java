package x10.trainup.auth.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Map<String, Object> memoryStore = new ConcurrentHashMap<>();

    public void setValue(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            System.err.println("⚠️ [REDIS FALLBACK] Redis unavailable, storing key in memory: " + key);
            memoryStore.put(key, value);
        }
    }

    public Object getValue(String key) {
        try {
            Object val = redisTemplate.opsForValue().get(key);
            if (val != null) return val;
        } catch (Exception e) {
            System.err.println("⚠️ [REDIS FALLBACK] Redis unavailable, getting key from memory: " + key);
        }
        return memoryStore.get(key);
    }

    public void deleteKey(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            // ignore
        }
        memoryStore.remove(key);
    }
}
