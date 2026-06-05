package com.nhpdev.backendservicesecond.service.impl;

import com.nhpdev.backendservicesecond.common.nhpenum.TokenType;
import com.nhpdev.backendservicesecond.configuration.JwtConfig;
import com.nhpdev.backendservicesecond.constraint.AppConstants;
import com.nhpdev.backendservicesecond.dto.request.AuthenticationRequest;
import com.nhpdev.backendservicesecond.dto.response.AuthenticationResponse;
import com.nhpdev.backendservicesecond.entity.Role;
import com.nhpdev.backendservicesecond.entity.User;
import com.nhpdev.backendservicesecond.entity.UserHasRole;
import com.nhpdev.backendservicesecond.exception.BackendServiceException;
import com.nhpdev.backendservicesecond.exception.ErrorCode;
import com.nhpdev.backendservicesecond.repository.UserRepository;
import com.nhpdev.backendservicesecond.service.AuthenticationService;
import com.nhpdev.backendservicesecond.service.JwtService;
import com.nhpdev.backendservicesecond.service.TokenService;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final JwtConfig jwtConfig;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (!(authenticate.getPrincipal() instanceof User user)) {
            throw new BackendServiceException(ErrorCode.USER_NOT_FOUND);
        }
        List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String userIdStr = user.getId();
        String sessionKey = UUID.randomUUID().toString();
        String accessToken = jwtService.generateAccessToken(userIdStr, authorities, sessionKey);
        String refreshToken = jwtService.generateRefreshToken(userIdStr, sessionKey);
        String refreshTokenJti;
        try {
            refreshTokenJti = SignedJWT.parse(refreshToken).getJWTClaimsSet().getJWTID();
        } catch (ParseException e) {
            log.error("Không thể trích xuất JTI từ Refresh Token của user: {}", userIdStr, e);
            throw new BackendServiceException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
        long expirationDuration = jwtConfig.getRefreshToken().getExpiration();
        tokenService.saveRefreshTokenJti(user.getId(), refreshTokenJti,
                sessionKey, expirationDuration, TimeUnit.DAYS);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .displayName(user.getDisplayName())
                .roles(user.getUserHasRoles().stream().map(UserHasRole::getRole)
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public AuthenticationResponse refreshToken(String refreshToken) {
        try {
            SignedJWT signedJWT = jwtService.validateToken(refreshToken, TokenType.REFRESH);
            String userId = signedJWT.getJWTClaimsSet().getSubject();
            String oldJti = signedJWT.getJWTClaimsSet().getJWTID();
            String sessionKey = signedJWT.getJWTClaimsSet()
                    .getStringClaim(AppConstants.SESSION_KEY_CLAIM);
            if (sessionKey == null) {
                throw new BackendServiceException(ErrorCode.TOKEN_INVALID);
            }
            if (!tokenService.isValidRefreshToken(userId, sessionKey, oldJti)) {
                log.warn("Cảnh báo bảo mật: Refresh Token hợp lệ chữ ký nhưng KHÔNG nằm trong Whitelist của session {}!", sessionKey);
                throw new BackendServiceException(ErrorCode.TOKEN_INVALID);
            }
            User user = userRepository.findById(userId).
                    orElseThrow(() -> new BackendServiceException(ErrorCode.TOKEN_INVALID));
            if (!user.isEnabled()) {
                throw new BackendServiceException(ErrorCode.USER_DISABLED);
            }
            List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            String newAccessToken = jwtService.generateAccessToken(userId, authorities, sessionKey);
            String newRefreshToken = jwtService.generateRefreshToken(userId, sessionKey);
            String newRefreshTokenJti = SignedJWT.parse(newRefreshToken).getJWTClaimsSet().getJWTID();
            long expirationDuration = jwtConfig.getRefreshToken().getExpiration();
            tokenService.saveRefreshTokenJti(user.getId(), newRefreshTokenJti,
                    sessionKey, expirationDuration, TimeUnit.DAYS);
            return AuthenticationResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
        } catch (ParseException e) {
            throw new BackendServiceException(ErrorCode.TOKEN_PARSE_FAILED);
        }
    }

    @Override
    public void logout(String accessToken) {
        SignedJWT signedJWT = jwtService.validateToken(accessToken, TokenType.ACCESS);
        try {
            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            String userId = signedJWT.getJWTClaimsSet().getSubject();
            String sessionKey = signedJWT.getJWTClaimsSet()
                    .getStringClaim(AppConstants.SESSION_KEY_CLAIM);
            if (sessionKey == null) {
                throw new BackendServiceException(ErrorCode.TOKEN_INVALID);
            }
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            long remainingTimeInMillis = expirationTime.getTime() - System.currentTimeMillis();
            if (remainingTimeInMillis > 0) {
                tokenService.blackListAccessToken(jti, remainingTimeInMillis, TimeUnit.MILLISECONDS);
            }
            tokenService.removeRefreshTokenOnSession(userId, sessionKey);
            log.info("User {} đã đăng xuất thành công khỏi thiết bị {}", userId, sessionKey);
        } catch (ParseException e) {
            throw new BackendServiceException(ErrorCode.TOKEN_PARSE_FAILED);
        }

    }
}
