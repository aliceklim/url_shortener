package org.example.service;

import org.example.repository.RedisCacheRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisCacheServiceTest {
    @Mock
    private RedisCacheRepository redisCacheRepository;

    @InjectMocks
    private RedisCacheService redisCacheService;

    private String hash;
    private String url;

    @BeforeEach
    void setUp() {
        hash = "abc123";
        url = "https://example.com";
    }

    @Test
    void getUrl(){
        when(redisCacheRepository.getUrl(hash)).thenReturn(url);

        String actualUrl = redisCacheService.getUrl(hash);

        Assertions.assertEquals(url, actualUrl);
        verify(redisCacheRepository, times(1)).getUrl(hash);
    }

    @Test
    void testSave(){
        redisCacheRepository.save(hash, url);

        verify(redisCacheRepository, times(1)).save(hash, url);
    }
}