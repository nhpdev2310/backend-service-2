package com.nhpdev.backendservicesecond.service.impl;

import com.nhpdev.backendservicesecond.common.nhpenum.TokenType;
import com.nhpdev.backendservicesecond.configuration.JwtConfig;
import com.nhpdev.backendservicesecond.dto.request.AuthenticationRequest;
import com.nhpdev.backendservicesecond.dto.response.AuthenticationResponse;
import com.nhpdev.backendservicesecond.entity.User;
import com.nhpdev.backendservicesecond.repository.UserRepository;
import com.nhpdev.backendservicesecond.service.AuthenticationService;
import com.nhpdev.backendservicesecond.service.JwtService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.getUserByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User is not exist"));
        String accessToken = jwtService.generateAccessToken(user.getId(), Set.of("MEMBER"));
        String refreshToken = jwtService.generateRefreshToken(user.getId());
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthenticationResponse refreshToken(String refreshToken) {
        try {
            SignedJWT signedJWT = jwtService.validateToken(refreshToken, TokenType.REFRESH);
            String userId = signedJWT.getJWTClaimsSet().getSubject();
            String newAccessToken = jwtService.generateAccessToken(userId, Set.of("MEMBER"));
            String newRefreshToken = jwtService.generateRefreshToken(userId);
            return AuthenticationResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logout(String accessToken) {
        // TODO Pending for logout feature
    }
}
