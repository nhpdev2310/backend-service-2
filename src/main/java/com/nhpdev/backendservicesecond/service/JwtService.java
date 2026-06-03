package com.nhpdev.backendservicesecond.service;

import com.nhpdev.backendservicesecond.common.nhpenum.TokenType;
import com.nimbusds.jwt.SignedJWT;

import java.util.Collection;
import java.util.List;

public interface JwtService {
    String generateAccessToken(String userId, Collection<String> authorities);
    String generateRefreshToken(String userId);
    String generateVerificationToken(String userId);
    SignedJWT validateToken(String token, TokenType tokenType);
    List<String> extractAuthorities(Object authoritiesClaims);
}
