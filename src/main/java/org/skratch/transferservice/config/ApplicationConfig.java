package org.skratch.transferservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Properties;

@Configuration
@Profile("!test")
public class ApplicationConfig {

    @Bean
    public Properties config() throws IOException {
        String secretName = System.getenv("SECRET_NAME");

        if (secretName != null) {
            Properties props = new Properties();
            props.setProperty("secret.name", secretName);
            return props;
        } else {
            String profile = System.getProperty("spring.profiles.active", "default");
            String fileName = "application-" + profile + ".properties";
            ClassPathResource resource = new ClassPathResource(fileName);

            if (!resource.exists()) {
                throw new IOException("Properties file not found: " + fileName);
            }

            Properties props = new Properties();
            props.load(resource.getInputStream());

            return props;
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
