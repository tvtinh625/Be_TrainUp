package x10.trainup.auth.infra.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import x10.trainup.commons.datasources.redis.ITokenStore;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisTokenStore implements ITokenStore {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveToken(String key, String token, long ttlMillis) {
        long ttlSeconds = Math.max(1, ttlMillis / 1000);
        redisTemplate.opsForValue().set(key, token, ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String getToken(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteToken(String key) {
        redisTemplate.delete(key);
    }
}
