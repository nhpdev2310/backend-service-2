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
    private AccessToken accessToken;
    private RefreshToken refreshToken;

    @Getter
    @Setter
    public static class AccessToken {
        private String secret;
        private long expiration;
    }

    @Getter
    @Setter
    public static class RefreshToken {
        private String secret;
        private long expiration;
    }
}
