package org.example.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.HashCleaner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final HashCleaner hashCleaner;

    @Scheduled(cron = "${cron_expression}")
    public void hashClear() {
        log.info("Hash cleaner started successfully");
        hashCleaner.hashClear();
    }
}
