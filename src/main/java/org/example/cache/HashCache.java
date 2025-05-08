package org.example.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Hash;
import org.example.service.HashGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    @Value("${queue-size}")
    private int queueSize;
    @Value("${percent}")
    private int fillPercent;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean filling = new AtomicBoolean(false);
    public Queue<Hash> hashes;

    @PostConstruct
    public void init() {
        this.hashes = new ArrayBlockingQueue<>(queueSize);
        List<String> generatedHashes = hashGenerator.getHashes(queueSize);
        log.info("Initialized hash cache with {} hashes", generatedHashes.size());
        for (String hash : generatedHashes) {
            if (hashes.offer(Hash.builder().hash(hash).build())) {
                log.info("Added hash queue: {}, total num of hashes available in queue: {}", hash, hashes.size());
            }
        }
    }

    public Hash getHash() {
        if (hashes.size() / (queueSize / 100.0) < fillPercent) {
            log.info("Less then {}% of hashes in queue, adding new hashes", fillPercent);
            if (filling.compareAndSet(false, true)) {
                fillHashes();
            }
        }
        return hashes.poll();
    }

    private void fillHashes() {
        log.info("Filling hashes in cache");
        hashGenerator.getHashesAsync(queueSize)
                .thenAccept(list -> hashes.addAll(
                        list.stream()
                                .map(h -> Hash.builder().hash(h).build())
                                .toList()
                ))
                .thenRun(() -> filling.set(false));
    }
}
