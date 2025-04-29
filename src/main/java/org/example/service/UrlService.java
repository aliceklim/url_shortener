package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cache.HashCache;
import org.example.dto.UrlDto;
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
        String hash = hashCache.getHash();
        String shortUrl = buildShortUrl(hash);

        redisCacheService.save(hash, urlDto.getUrl());
        urlRepository.save(hash, urlDto.getUrl());

        log.info("Short url was successfully created: {}", shortUrl);
        return shortUrl;
    }

    public String getOriginalUrl(String hash) {
        String cacheUrl = redisCacheService.getUrl(hash);
        if (!cacheUrl.isEmpty()) {
            return cacheUrl;
        }

        Optional<String> urlByHash = urlRepository.findUrlByHash(hash);
        if (urlByHash.isPresent()) {
            return urlByHash.get();
        } else {
            log.error("Hash doesn't exist");
            throw new NotFoundException("Hash doesn't exist");
        }
    }

    private String buildShortUrl(String hash) {
        return SERVICE_URL + hash;
    }
}
