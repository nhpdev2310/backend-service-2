package com.nhpdev.backendservicesecond.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig {
    private String issuer;
    private String audience;
    private SecretKey accessToken;
    private SecretKey refreshToken;
    private SecretKey verificationToken;

    @Getter
    @Setter
    public static class SecretKey {
        private String secret;
        private long expiration;
    }

}
