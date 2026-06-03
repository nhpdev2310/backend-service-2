package com.nhpdev.backendservicesecond.service.impl;

import com.nhpdev.backendservicesecond.common.nhpenum.TokenType;
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
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

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
        String accessToken = jwtService.generateAccessToken(user.getId(), authorities);
        String refreshToken = jwtService.generateRefreshToken(user.getId());
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
            User user = userRepository.findById(userId).
                    orElseThrow(() -> new BackendServiceException(ErrorCode.TOKEN_INVALID));
            List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            String newAccessToken = jwtService.generateAccessToken(userId, authorities);
            String newRefreshToken = jwtService.generateRefreshToken(userId);
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
        // TODO Pending for logout feature
    }
}
