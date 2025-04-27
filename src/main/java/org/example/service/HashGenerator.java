package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Hash;
import org.example.repository.HashRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private static final int ENCODING_FACTOR = 62;
    private static final String BASE_62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final HashRepository hashRepository;
    @Value("${batchSize}")
    private int batchSize;
    @Value("${uniqueNumbers}")
    private Long uniqueNumber;

    public void generateBatch() {
        Set<Long> uniqueNumbers = hashRepository.getUniqueNumbers(uniqueNumber);
        List<Hash> hashes = uniqueNumbers.stream()
                .map(this::encode)
                .map(hash -> Hash.builder().hash(hash).build())
                .collect(Collectors.toList());
        saveBatch(hashes);
        log.info("{} hashes successfully generated ", hashes);
    }

    @Transactional
    public List<String> getHashes(long amount) {
        List<Hash> hashBatch = hashRepository.getHashAndDelete(amount);
        if (hashBatch.size() < amount) {
            generateBatch();
            hashBatch.addAll(hashRepository.getHashAndDelete(amount - hashBatch.size()));
        }
        saveBatch(hashBatch);
        return hashBatch.stream()
                .map(Hash::getHash)
                .collect(Collectors.toList());
    }

    @Async("batchExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    private String encode(long number) {
        StringBuilder sb = new StringBuilder();
        do {
            int remainder = (int) (number % ENCODING_FACTOR);
            sb.insert(0, BASE_62_ALPHABET.charAt(remainder));
            number /= ENCODING_FACTOR;
        } while (number > 0);
        return sb.toString();
    }

    private List<Hash> saveBatch(List<Hash> hashes) {
        List<Hash> result = new ArrayList<>(hashes.size());
        for (int i = 0; i < hashes.size(); i += batchSize) {
            int size = i + batchSize;
            if (size > hashes.size()) {
                size = hashes.size();
            }
            List<Hash> sub = hashes.subList(i, size);
            hashRepository.saveAll(sub);
            result.addAll(sub);
        }
        log.info("{} hashes were successfully saved ", hashes);

        return result;
    }
}
