package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Hash;
import org.example.repository.HashRepository;
import org.example.util.Base62Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${batchSize}")
    private int batchSize;
    @Value("${uniqueNumbers}")
    private Long uniqueNumber;

    @Transactional
    public List<String> getHashes(long amount) {
        log.info("Getting {} hashes from DB", amount);
        List<Hash> hashBatch = hashRepository.getHashAndDelete(amount);
        if (hashBatch.size() < amount) {
            log.info("Not enough hashes in DB: {}, generating new batch", hashBatch.size());
            generateBatch();
            hashBatch.addAll(hashRepository.getHashAndDelete(amount - hashBatch.size()));
        }
        saveBatch(hashBatch);
        return hashBatch.stream()
                .map(Hash::getHash)
                .collect(Collectors.toList());
    }

    public void generateBatch() {
        Set<Long> uniqueNumbers = hashRepository.getUniqueNumbers(uniqueNumber);
        List<Hash> hashes = uniqueNumbers.stream()
                .map(Base62Encoder::encode)
                .distinct()
                .map(hash -> Hash.builder().hash(hash).build())
                .collect(Collectors.toList());
        log.info("{} hashes successfully generated ", hashes.size());
        saveBatch(hashes);
    }

    @Async("batchExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    public void saveBatch(List<Hash> hashes) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < hashes.size(); i += batchSize) {
            int end = Math.min(i + batchSize, hashes.size());
            List<Hash> sub = hashes.subList(i, end);
            hashRepository.saveAll(sub);
        }
        log.info("{} hashes were successfully saved to DB in {} ms", hashes.size(), System.currentTimeMillis() - start);
    }
}
