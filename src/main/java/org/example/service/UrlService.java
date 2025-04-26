package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cache.HashCache;
import org.example.dto.UrlDto;
import org.example.exception.NotFoundException;
import org.example.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    private final HashCache hashCache;
    private final RedisCacheService redisCacheService;
    private final UrlRepository urlRepository;

    @Transactional
    public String getShortUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        String shortUrl = buildShortUrl(urlDto, hash).orElseThrow();

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

    private Optional<String> buildShortUrl(UrlDto urlDto, String hash) {
        URL url = null;
        try {
            url = new URL(urlDto.getUrl());
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
        String protocol = url.getProtocol();
        String host = url.getHost();
        int port = url.getPort();

        String portString = (port == -1) ? "" : ":" + port;

        return Optional.of(protocol + "://" + host + portString + "/" + hash);
    }
}
