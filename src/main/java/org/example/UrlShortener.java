package org.example;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@EnableFeignClients("school.faang.servicetemplate.client")
public class UrlShortener {
    public static void main(String[] args) {
        new SpringApplicationBuilder(UrlShortener.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}