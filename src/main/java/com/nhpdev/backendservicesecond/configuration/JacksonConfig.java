package com.nhpdev.backendservicesecond.configuration;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.cfg.DateTimeFeature;

@Configuration
public class JacksonConfig {
    @Bean
    JsonMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
