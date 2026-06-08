package com.nhpdev.backendservicesecond.service.impl;

import static com.nhpdev.backendservicesecond.constraint.RedisConstant.*;
import static com.nhpdev.backendservicesecond.constraint.AppConstants.*;

import com.nhpdev.backendservicesecond.configuration.JwtConfig;
import com.nhpdev.backendservicesecond.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "TOKEN_SERVICE")
public class TokenServiceImpl implements TokenService {

    private final StringRedisTemplate stringRedisTemplate;
    private final JwtConfig jwtConfig;

    /**
     * @param userId
     * @param jti
     * @param sessionKey
     * @param duration
     * @param unit
     */
    @Override
    public void saveRefreshTokenJti(String userId, String jti, String sessionKey,
                                    long duration, TimeUnit unit) {
        String key = WHITELIST_REFRESH_PREFIX + userId + ":" + sessionKey;
        stringRedisTemplate.opsForValue().set(key, jti, duration, unit);
    }

    /**
     * @param userId
     * @param sessionKey
     * @param requestJti
     * @return
     */
    @Override
    public boolean isValidRefreshToken(String userId, String sessionKey, String requestJti) {
        String key = WHITELIST_REFRESH_PREFIX + userId + ":" + sessionKey;
        String storedJti = stringRedisTemplate.opsForValue().get(key);
        return StringUtils.hasText(storedJti) && storedJti.equals(requestJti);
    }

    /**
     * @param userId
     * @param sessionKey
     */
    @Override
    public void removeRefreshTokenOnSession(String userId, String sessionKey) {
        String key = WHITELIST_REFRESH_PREFIX + userId + ":" + sessionKey;
        stringRedisTemplate.delete(key);
        log.info("Removed refresh token from whitelist for user: {}, sessionKey: {}", userId, sessionKey);
    }

    /**
     * @param userId
     */
    @Override
    public void removeAllRefreshTokensOfUser(String userId) {
        String patten = WHITELIST_REFRESH_PREFIX + userId + ":*";
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            ScanOptions options = ScanOptions.scanOptions().match(patten).count(SCAN_OPTION_COUNT).build();
            try (Cursor<byte[]> cursor = connection.keyCommands().scan(options)) {
                List<byte[]> keysToDelete = new ArrayList<>();
                while (cursor.hasNext()) {
                    keysToDelete.add(cursor.next());
                }
                if(!keysToDelete.isEmpty()) {
                    connection.keyCommands().del(keysToDelete.toArray(new byte[0][]));
                }
            } catch (Exception e) {
                log.error("Error occurred while scanning and deleting all refresh tokens for user: {}", userId, e);
            }
            return null;
        });
        log.info("Removed all refresh tokens from whitelist for user: {}", userId);
    }

    /**
     * @param userId
     */
    @Override
    public void saveLogoutAllTimestamp(String userId) {
        String key = LOGOUT_ALL_PREFIX + userId;
        long currentTimestamp = System.currentTimeMillis();
        long redisTtlInMinutes = jwtConfig.getAccessToken().getExpiration() + 5;
        stringRedisTemplate.opsForValue().set(key,
                String.valueOf(currentTimestamp),
                redisTtlInMinutes, TimeUnit.MINUTES);
        this.removeAllRefreshTokensOfUser(userId);
        log.info("Logout of all device successfully for user: {}", userId);
    }

    /**
     * @param userId
     * @param issueAt
     * @return
     */
    @Override
    public boolean isTokenIssuedBeforeLogoutAll(String userId, Date issueAt) {
        String key = LOGOUT_ALL_PREFIX + userId;
        String logoutTimeStampStr = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(logoutTimeStampStr)) {
            return false;
        }
        long logoutTimeStamp = Long.parseLong(logoutTimeStampStr);
        return issueAt.getTime() < logoutTimeStamp;
    }

    /**
     * @param jti
     * @param remainingTime in Milliseconds
     * @param unit
     */
    @Override
    public void blackListAccessToken(String jti, long remainingTime, TimeUnit unit) {
        if (remainingTime > 0) {
            String key = BLACKLIST_ACCESS_PREFIX + jti;
            stringRedisTemplate.opsForValue().set(key, "invalid", remainingTime, unit);
            log.info("Blacklisted access token JTI: {}", jti);
        }
    }

    /**
     * @param jti
     * @return
     */
    @Override
    public boolean isAccessTokenBlackListed(String jti) {
        String key = BLACKLIST_ACCESS_PREFIX + jti;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public String createVerificationToken(String userId) {
        String token = UUID.randomUUID().toString();
        String key = VERIFY_EMAIL_PREFIX + token;
        stringRedisTemplate.opsForValue().set(key, userId, VERIFY_TOKEN_EXPIRATION_IN_DAY, TimeUnit.DAYS);
        log.info("Created email verification token for user: {}", userId);
        return token;
    }

    /**
     * @param token
     * @return
     */
    @Override
    public String validateAndConsumeVerificationToken(String token) {
        String key = VERIFY_EMAIL_PREFIX + token;
        String userId = stringRedisTemplate.opsForValue().get(key);
        if(userId != null) {
            stringRedisTemplate.delete(key);
            stringRedisTemplate.hasKey(key);
            return userId;
        }
        log.warn("Invalid or expired verification token used: {}", token);
        return null;
    }
}