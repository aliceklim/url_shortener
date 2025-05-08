package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cache.HashCache;
import org.example.dto.UrlDto;
import org.example.entity.Hash;
import org.example.exception.NotFoundException;
import org.example.repository.UrlRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final HashCache hashCache;
    private final RedisCacheService redisCacheService;
    private final UrlRepository urlRepository;
    private static final String SERVICE_URL = "https://magicurl.com/";

    @Transactional
    public String generateShortUrl(UrlDto urlDto) {
        Hash hash = hashCache.getHash();
        String shortUrl = buildShortUrl(hash);

        redisCacheService.save(hash.getHash(), urlDto.getUrl());
        urlRepository.save(hash.getHash(), urlDto.getUrl());

        log.info("Short url was successfully created: {}", shortUrl);
        return shortUrl;
    }

    public String getOriginalUrl(String hash) {
        String cacheUrl = redisCacheService.getUrl(hash);
        if (!cacheUrl.isEmpty()) {
            log.info("Url was found in redis cache: {}", cacheUrl);
            return cacheUrl;
        }

        Optional<String> urlByHash = urlRepository.findUrlByHash(hash);
        if (urlByHash.isPresent()) {
            log.info("Url was found in DB: {}", urlByHash.get());
            return urlByHash.get();
        } else {
            log.error("Hash doesn't exist");
            throw new NotFoundException("Hash doesn't exist");
        }
    }

    private String buildShortUrl(Hash hash) {
        return SERVICE_URL + hash.getHash();
    }
}
