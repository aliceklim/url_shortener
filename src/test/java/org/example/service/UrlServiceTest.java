package org.example.service;

import org.example.cache.HashCache;
import org.example.dto.UrlDto;
import org.example.entity.Hash;
import org.example.exception.NotFoundException;
import org.example.repository.UrlRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private RedisCacheService redisCacheService;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    private UrlDto urlDto;
    private Hash hash;
    private String generatedUrl;
    private String url;

    @BeforeEach
    void setUp() {
        urlDto = new UrlDto("https://example.com");
        hash = Hash.builder().hash("abc123").build();
        generatedUrl = "https://magicurl.com/abc123";
        url = "https://example.com";
    }

    @Test
    void testGenerateShortUrl() {
        when(hashCache.getHash()).thenReturn(hash);

        String shortUrl = urlService.generateShortUrl(urlDto);

        Assertions.assertEquals(generatedUrl, shortUrl);
        verify(redisCacheService).save(hash.getHash(), urlDto.getUrl());
        verify(urlRepository).save(hash.getHash(), urlDto.getUrl());
    }

    @Test
    void testGetOriginalUrl_FoundInCache() {
        when(redisCacheService.getUrl(hash.getHash())).thenReturn(url);

        String result = urlService.getOriginalUrl(hash.getHash());

        Assertions.assertEquals(url, result);
        verify(redisCacheService).getUrl(hash.getHash());
        verify(urlRepository, never()).findUrlByHash(anyString());
    }

    @Test
    void testGetOriginalUrl_FoundInDB() {
        when(redisCacheService.getUrl(hash.getHash())).thenReturn("");
        when(urlRepository.findUrlByHash(hash.getHash())).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(hash.getHash());

        Assertions.assertEquals(url, result);
        verify(urlRepository).findUrlByHash(hash.getHash());
    }

    @Test
    void testGetOriginalUrl_NotFoundException() {
        when(redisCacheService.getUrl(hash.getHash())).thenReturn("");
        when(urlRepository.findUrlByHash(hash.getHash())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> urlService.getOriginalUrl(hash.getHash()));

        verify(urlRepository).findUrlByHash(hash.getHash());
    }
}