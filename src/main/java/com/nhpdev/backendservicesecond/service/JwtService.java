package com.nhpdev.backendservicesecond.service;

import com.nhpdev.backendservicesecond.common.nhpenum.TokenType;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;

public interface JwtService {
    String generateAccessToken(String userId, Collection<String> authorities);
    String generateRefreshToken(String userId);
    SignedJWT validateToken(String token, TokenType tokenType) throws ParseException, JOSEException;
    List<String> extractAuthorities(Object authoritiesClaims);
}
