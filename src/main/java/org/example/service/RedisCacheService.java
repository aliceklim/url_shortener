package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.RedisCacheRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {
    private final RedisCacheRepository redisCacheRepository;

    @Cacheable(value = "hashCash", key = "#hash")
    public String getUrl(String hash) {
        return redisCacheRepository.getUrl(hash);
    }

    public void save(String hash, String url) {
        redisCacheRepository.save(hash, url);
    }
}
