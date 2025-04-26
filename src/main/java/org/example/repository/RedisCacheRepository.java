package org.example.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Slf4j
@Repository
public class RedisCacheRepository{
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
        log.info("Url was successfully saved {}", url);
    }

    public String getUrl(String hash) {
        String hashCache = redisTemplate.opsForValue().get(hash);
        if (hashCache != null) {
            log.warn("Hash {} found in cache", hash);
        } else {
            log.warn("Hash {} doesn't exist", hash);
        }
        return hashCache;
    }
}
