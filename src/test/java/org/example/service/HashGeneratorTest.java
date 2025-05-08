package org.example.service;

import org.example.entity.Hash;
import org.example.repository.HashRepository;
import org.example.util.Base62Encoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    private String hash1;
    private String hash2;
    private String hash3;
    private String hash4;

    @Captor
    private ArgumentCaptor<List<Hash>> hashListCaptor;

    @BeforeEach
    void setUp() throws Exception {
        setField(hashGenerator, "batchSize", 2);
        setField(hashGenerator, "uniqueNumber", 100L);
        hash1 = "abc";
        hash2 = "def";
        hash3 = "ghi";
        hash4 = "jkl";
    }

    private void setField(Object object, String name, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    void testGetHashes_WhenEnoughHashesInDB() {
        List<String> hashes = List.of(hash1, hash2);
        when(hashRepository.getHashAndDelete(2)).thenReturn(hashes);

        List<String> result = hashGenerator.getHashes(2);

        assertEquals(List.of("abc", "def"), result);
        verify(hashRepository).saveAll(anyList());
    }

    @Test
    void testGetHashes_WhenNotEnoughHashes() {
        when(hashRepository.getHashAndDelete(2)).thenReturn(new ArrayList<>(List.of(hash1)));
        when(hashRepository.getUniqueNumbers(100L)).thenReturn(Set.of(1L));
        when(base62Encoder.encode(1L)).thenReturn("def");
        when(hashRepository.getHashAndDelete(1)).thenReturn(List.of(hash2));

        List<String> result = hashGenerator.getHashes(2);

        assertEquals(List.of("abc", "def"), result);
        verify(hashRepository, times(2)).getHashAndDelete(anyLong());
        verify(hashRepository).getUniqueNumbers(100L);
        verify(base62Encoder).encode(1L);
        verify(hashRepository, times(2)).saveAll(anyList());
    }

    @Test
    void testGenerateBatch() {
        when(hashRepository.getUniqueNumbers(100L)).thenReturn(Set.of(123L, 456L));
        when(base62Encoder.encode(123L)).thenReturn("abc");
        when(base62Encoder.encode(456L)).thenReturn("def");

        hashGenerator.generateBatch();

        verify(hashRepository).saveAll(hashListCaptor.capture());
        List<String> savedHashes = hashListCaptor.getValue().stream()
                .map(Hash::getHash)
                .toList();

        Assertions.assertTrue(savedHashes.containsAll(List.of("abc", "def")));
        assertEquals(2, savedHashes.size());
    }

    @Test
    void testSaveBatch_SplitsCorrectly() {
        List<String> hashes = List.of(hash1, hash2, hash3, hash4);

        hashGenerator.saveBatch(hashes.stream()
                .map(hash -> Hash.builder().hash(hash).build())
                .toList());

        verify(hashRepository, times(2)).saveAll(anyList());
    }

    @Test
    void testGetHashesAsync() throws Exception {
        List<String> hashes = List.of(hash1);
        when(hashRepository.getHashAndDelete(1)).thenReturn(hashes);

        CompletableFuture<List<String>> future = hashGenerator.getHashesAsync(1);
        assertEquals(List.of("abc"), future.get());
    }
}
