package com.nhpdev.backendservicesecond;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BackendServiceSecondApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendServiceSecondApplication.class, args);
    }
}
