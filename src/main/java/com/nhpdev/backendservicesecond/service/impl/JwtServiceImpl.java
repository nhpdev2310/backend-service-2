package com.nhpdev.backendservicesecond.service.impl;

import com.nhpdev.backendservicesecond.common.nhpenum.TokenType;
import com.nhpdev.backendservicesecond.configuration.JwtConfig;
import com.nhpdev.backendservicesecond.exception.BackendServiceException;
import com.nhpdev.backendservicesecond.exception.ErrorCode;
import com.nhpdev.backendservicesecond.service.JwtService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final JwtConfig jwtConfig;
    private Map<TokenType, String> tokenSecretMap;

    @PostConstruct
    public void init() {
        this.tokenSecretMap = Map.of(
                TokenType.ACCESS, jwtConfig.getAccessToken().getSecret(),
                TokenType.REFRESH, jwtConfig.getRefreshToken().getSecret(),
                TokenType.VERIFICATION, jwtConfig.getVerificationToken().getSecret()
        );
    }

    @Override
    public String generateAccessToken(String userId, Collection<String> authorities) {
        return signAndSerialize(userId, TokenType.ACCESS,
                jwtConfig.getAccessToken().getExpiration(), ChronoUnit.DAYS, Map.of("authorities", authorities));
    }

    @Override
    public String generateRefreshToken(String userId) {
        return signAndSerialize(userId, TokenType.REFRESH,
                jwtConfig.getRefreshToken().getExpiration(), ChronoUnit.DAYS, Map.of());
    }

    @Override
    public String generateVerificationToken(String userId) {
        return signAndSerialize(userId, TokenType.VERIFICATION,
                jwtConfig.getVerificationToken().getExpiration(), ChronoUnit.DAYS, Map.of());
    }

    @Override
    public SignedJWT validateToken(String token, TokenType tokenType){
        if (token == null || token.isBlank())
            throw new BackendServiceException(ErrorCode.TOKEN_MISSING);
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String secret = tokenSecretMap.get(tokenType);
            if (secret == null) {
                log.error("Missing secret configuration for TokenType: {}", tokenType);
                throw new BackendServiceException(ErrorCode.UNSUPPORTED_TOKEN_TYPE);
            }
            JWSVerifier verifier = new MACVerifier(secret);
            if (!signedJWT.verify(verifier)) {
                log.warn("Signature verification failed for token type: {}", tokenType);
                throw new BackendServiceException(ErrorCode.TOKEN_INVALID);
            }
            Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiredTime != null && expiredTime.before(new Date())) {
                log.warn("Token type {} has expired at {}", tokenType, expiredTime);
                throw new BackendServiceException(ErrorCode.TOKEN_EXPIRED);
            }
            return signedJWT;
        } catch (ParseException e) {
            log.error("Failed to parse JWT token string", e);
            throw new BackendServiceException(ErrorCode.TOKEN_INVALID);
        } catch (JOSEException e) {
            log.error("Crypto context error during token verification", e);
            throw new BackendServiceException(ErrorCode.TOKEN_VERIFICATION_FAILED);
        }
    }

    @Override
    public List<String> extractAuthorities(Object authoritiesClaims) {
        if (authoritiesClaims == null)
            return List.of();
        else if(authoritiesClaims instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    private String signAndSerialize(String userId, TokenType tokenType,
                                    long expirationDuration, ChronoUnit unit,
                                    Map<String, Object> customClaims
                                    ) {

        Date issueTime = new Date();
        Date expirationTime = Date.from(issueTime.toInstant().plus(expirationDuration, unit));
        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(userId)
                .issuer(jwtConfig.getIssuer())
                .issueTime(issueTime)
                .expirationTime(expirationTime)
                .audience(jwtConfig.getAudience())
                .jwtID(UUID.randomUUID().toString());
        customClaims.forEach(claimsBuilder::claim);
        JWTClaimsSet claimsSet = claimsBuilder.build();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        try {
            String secret = tokenSecretMap.get(tokenType);
            signedJWT.sign(new MACSigner(secret));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("Error occurred while signing {} token", tokenType, e);
            throw new BackendServiceException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
    }
}
