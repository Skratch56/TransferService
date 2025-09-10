package org.skratch.transferservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@Slf4j
@SpringBootApplication
public class TransferServiceApplication {

    public static void main(String[] args) {
        String env = System.getProperty("ENV");
        if (env == null || env.isEmpty()) {
            env = System.getenv("ENV");
        }
        if (env == null || env.isEmpty()) {
            env = "dev";
        }

        System.setProperty("spring.profiles.active", env);

        ConfigurableApplicationContext context = new SpringApplicationBuilder(TransferServiceApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);

        if (context.isRunning()) {
            log.info("Application started with profile: {}", env);
        }
    }

}
