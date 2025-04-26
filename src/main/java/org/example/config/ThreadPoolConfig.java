package org.example.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "moderation-thread-pool")
public class ThreadPoolConfig {
    @Value("${thread-pool.queueCapacity}")
    private int queueCapacity;
    @Value("${thread-pool.maxPoolSize}")
    private int maxPoolSize;
    @Value("${thread-pool.corePoolSize}")
    private int corePoolSize;
    @Value("${thread-pool.threadNamePrefix}")
    private String threadNamePrefix;
}
