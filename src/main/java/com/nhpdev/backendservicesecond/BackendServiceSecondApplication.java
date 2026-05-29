package com.nhpdev.backendservicesecond;

import com.nhpdev.backendservicesecond.configuration.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BackendServiceSecondApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendServiceSecondApplication.class, args);
    }
}
