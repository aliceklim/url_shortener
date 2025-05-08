package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Hash;
import org.example.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashCleaner {
    private final HashGenerator hashGenerator;
    private final UrlRepository urlRepository;
    @Value("${url-cleanup.expiration-threshold}")
    private String expirationThreshold;

    @Transactional
    public void hashClear() {
        Period period = Period.parse(expirationThreshold);
        LocalDateTime cutoff = LocalDateTime.now().minus(period);
        List<String> hashes = urlRepository.deleteExpiredHashes(cutoff);
        hashGenerator.saveBatch(hashes.stream().map(hash -> Hash.builder().hash(hash).build()).toList());
        log.info("Hash cleaner deleted {} hashes from url table", hashes.size());
    }
}
