package org.example;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
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