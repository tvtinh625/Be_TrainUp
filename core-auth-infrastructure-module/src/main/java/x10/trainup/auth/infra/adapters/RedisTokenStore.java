package x10.trainup.auth.infra.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import x10.trainup.commons.datasources.redis.ITokenStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisTokenStore implements ITokenStore {

    private final StringRedisTemplate redisTemplate;
    private final Map<String, TokenItem> fallbackStore = new ConcurrentHashMap<>();

    private record TokenItem(String token, long expireAt) {}

    @Override
    public void saveToken(String key, String token, long ttlMillis) {
        long ttlSeconds = Math.max(1, ttlMillis / 1000);
        try {
            redisTemplate.opsForValue().set(key, token, ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("⚠️ [REDIS FALLBACK] Redis unavailable, using in-memory store for key: " + key);
            fallbackStore.put(key, new TokenItem(token, System.currentTimeMillis() + ttlMillis));
        }
    }

    @Override
    public String getToken(String key) {
        try {
            String val = redisTemplate.opsForValue().get(key);
            if (val != null) return val;
        } catch (Exception e) {
            System.err.println("⚠️ [REDIS FALLBACK] Redis unavailable, fetching key from in-memory store: " + key);
        }

        TokenItem item = fallbackStore.get(key);
        if (item != null) {
            if (System.currentTimeMillis() < item.expireAt()) {
                return item.token();
            } else {
                fallbackStore.remove(key);
            }
        }
        return null;
    }

    @Override
    public void deleteToken(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            // ignore
        }
        fallbackStore.remove(key);
    }
}
