package com.nhpdev.backendservicesecond.security.validator;

import com.nhpdev.backendservicesecond.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
@Slf4j
public class JwtRedisValidator implements OAuth2TokenValidator<Jwt> {

    private final TokenService tokenService;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        String jti = token.getId();
        String userId = token.getSubject();
        Instant issuedAtInstant = token.getIssuedAt();
        log.debug("=== DEBUG SECURITY === Đang check Blacklist cho token JTI: {}", jti);
        if (tokenService.isAccessTokenBlackListed(jti)) {
            log.warn("Token mang JTI {} đã bị blacklist (User đã logout đơn lẻ)", jti);
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Token đã bị đăng xuất khỏi thiết bị này.", null)
            );
        }

        // 2. Chốt chặn 2: Kiểm tra Logout All (Nếu bạn đã thêm hàm check timestamp vào TokenService)
        // Nếu chưa làm tính năng Logout All, bạn có thể tạm thời comment đoạn dưới lại
        if (issuedAtInstant != null) {
            Date issuedAt = Date.from(issuedAtInstant);
            if (tokenService.isTokenIssuedBeforeLogoutAll(userId, issuedAt)) {
                log.warn("Token của user {} bị từ chối vì sinh ra trước thời điểm Logout All", userId);
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("token_expired", "Phiên đăng nhập đã hết hạn trên toàn hệ thống.", null)
                );
            }
        }
        return OAuth2TokenValidatorResult.success();
    }
}
