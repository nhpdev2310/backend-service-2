package com.nhpdev.backendservicesecond.service;

import com.nhpdev.backendservicesecond.common.nhpenum.TokenType;
import com.nimbusds.jwt.SignedJWT;

import java.util.Collection;
import java.util.List;

public interface JwtService {
    String generateAccessToken(String userId, Collection<String> authorities, String sessionKey);
    String generateRefreshToken(String userId, String sessionKey);
    SignedJWT validateToken(String token, TokenType tokenType);
    List<String> extractAuthorities(Object authoritiesClaims);
}
