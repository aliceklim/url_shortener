package org.example.service;

import org.example.entity.Hash;
import org.example.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashCleanerTest {
    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private HashCleaner hashCleaner;

    private String hash1;
    private String hash2;

    @BeforeEach
    void setUp() throws Exception {
        Field field = HashCleaner.class.getDeclaredField("expirationThreshold");
        field.setAccessible(true);
        field.set(hashCleaner, "P1D");
        hash1 = "abc123";
        hash2 = "xyz789";
    }

    @Test
    void testHashClear() {
        List<String> hashes = List.of(hash1, hash2);
        when(urlRepository.deleteExpiredHashes(any(LocalDateTime.class))).thenReturn(hashes);
        List<Hash> expectedHashes = hashes.stream()
                .map(h -> Hash.builder().hash(h).build())
                .toList();

        hashCleaner.hashClear();

        verify(urlRepository, times(1)).deleteExpiredHashes(any(LocalDateTime.class));
        verify(hashGenerator, times(1)).saveBatch(expectedHashes);
    }
}
