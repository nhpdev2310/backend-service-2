package com.nhpdev.backendservicesecond.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface TokenService {
    void saveRefreshTokenJti(String userId, String jti, String sessionKey, long duration, TimeUnit unit);
    boolean isValidRefreshToken(String userId, String sessionKey, String requestJti);
    void removeRefreshTokenOnSession(String userId, String sessionKey);
    void blackListAccessToken(String jti, long remainingTime, TimeUnit unit);
    boolean isAccessTokenBlackListed(String jti);
    String createVerificationToken(String userId);
    String validateAndConsumeVerificationToken(String token);
    void removeAllRefreshTokensOfUser(String userId);
    void saveLogoutAllTimestamp(String userId);
    boolean isTokenIssuedBeforeLogoutAll(String userId, Date issueAt);

}
