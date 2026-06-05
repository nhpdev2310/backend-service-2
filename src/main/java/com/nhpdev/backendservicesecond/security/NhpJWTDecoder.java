package com.nhpdev.backendservicesecond.security;

import com.nhpdev.backendservicesecond.constraint.JwtConstants;
import com.nhpdev.backendservicesecond.configuration.JwtConfig;
import com.nhpdev.backendservicesecond.security.validator.JwtRedisValidator;
import com.nhpdev.backendservicesecond.service.TokenService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class NhpJWTDecoder implements JwtDecoder {

    private NimbusJwtDecoder nimbusJwtDecoder;
    private final TokenService tokenService;
    private final JwtConfig jwtConfig;

    @PostConstruct
    public void init() {
        SecretKey secretKey = new SecretKeySpec(
                jwtConfig.getAccessToken().getSecret().getBytes(StandardCharsets.UTF_8),
                JwtConstants.ALGORITHM
        );
        this.nimbusJwtDecoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        OAuth2TokenValidator<Jwt> combinedValidator = JwtValidators.createDefaultWithValidators(
                new JwtIssuerValidator(jwtConfig.getIssuer()),
                new JwtAudienceValidator(jwtConfig.getAudience()),
                new JwtRedisValidator(tokenService)
        );
        this.nimbusJwtDecoder.setJwtValidator(combinedValidator);
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        return nimbusJwtDecoder.decode(token);
    }
}
