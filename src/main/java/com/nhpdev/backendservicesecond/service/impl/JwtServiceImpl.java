package com.nhpdev.backendservicesecond.service.impl;

import com.nhpdev.backendservicesecond.common.nhpenum.TokenType;
import com.nhpdev.backendservicesecond.configuration.JwtConfig;
import com.nhpdev.backendservicesecond.service.JwtService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final JwtConfig jwtConfig;

    @Override
    public String generateAccessToken(String userId, Collection<String> authorities) {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();
        Date issueTime = new Date();
        Date expirationTime = Date.from(issueTime.toInstant()
                .plus(jwtConfig.getAccessToken().getExpiration(), ChronoUnit.HOURS));
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userId)
                .issuer(jwtConfig.getIssuer())
                .issueTime(issueTime)
                .expirationTime(expirationTime)
                .audience(jwtConfig.getAudience())
                .claim("authorities", authorities)
                .jwtID(UUID.randomUUID().toString())
                .build();
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        try {
            signedJWT.sign(new MACSigner(jwtConfig.getAccessToken().getSecret()));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateRefreshToken(String userId) {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();
        Date issueTime = new Date();
        Date expirationTime = Date.from(issueTime.toInstant()
                .plus(jwtConfig.getRefreshToken().getExpiration(), ChronoUnit.DAYS));
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userId)
                .issuer(jwtConfig.getIssuer())
                .issueTime(issueTime)
                .expirationTime(expirationTime)
                .audience(jwtConfig.getAudience())
                .jwtID(UUID.randomUUID().toString())
                .build();
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        try {
            signedJWT.sign(new MACSigner(jwtConfig.getRefreshToken().getSecret()));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SignedJWT validateToken(String token, TokenType tokenType) throws ParseException, JOSEException {
        if (token == null || token.isBlank())
            throw new RuntimeException("Token is missing");
        SignedJWT signedJWT = SignedJWT.parse(token);
        boolean isValid = signedJWT.verify(new MACVerifier(
                tokenType == TokenType.ACCESS ? jwtConfig.getAccessToken().getSecret()
                : jwtConfig.getRefreshToken().getSecret()));
        if(!isValid)
            throw new RuntimeException("Token is invalid");
        Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expiredTime.before(new Date()))
            throw new RuntimeException("Token is expired");
        return signedJWT;
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
}
