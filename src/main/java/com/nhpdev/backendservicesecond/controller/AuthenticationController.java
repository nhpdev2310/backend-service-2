package com.nhpdev.backendservicesecond.controller;

import com.nhpdev.backendservicesecond.constraint.AppConstants;
import com.nhpdev.backendservicesecond.configuration.JwtConfig;
import com.nhpdev.backendservicesecond.constraint.JwtConstants;
import com.nhpdev.backendservicesecond.dto.request.AuthenticationRequest;
import com.nhpdev.backendservicesecond.dto.response.ApiResponse;
import com.nhpdev.backendservicesecond.dto.response.AuthenticationResponse;
import com.nhpdev.backendservicesecond.dto.response.LoginResponse;
import com.nhpdev.backendservicesecond.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping(AppConstants.URL_PREFIX + "/auth")
public class AuthenticationController {

    private final AuthenticationService authService;
    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody AuthenticationRequest request,
                                            HttpServletResponse response) {
        var data = authService.authenticate(request);
        ResponseCookie cookie = getResponseCookie(data);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ApiResponse.success(LoginResponse.of(data));
    }


    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refreshToken(@CookieValue(name = "refresh_token") String refreshToken,
                                                            HttpServletResponse response) {
        var data = authService.refreshToken(refreshToken);
        ResponseCookie cookie = getResponseCookie(data);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ApiResponse.success(LoginResponse.of(data));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authorizationHeader,
                                    HttpServletResponse response) {
        String accessToken = authorizationHeader.replace(JwtConstants.BEARER_PREFIX, "");
        authService.logout(accessToken);
        ResponseCookie clearCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());
        return ApiResponse.noContent();
    }

    private @NonNull ResponseCookie getResponseCookie(AuthenticationResponse data) {
        return ResponseCookie.from("refresh_token", data.getRefreshToken())
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(Duration.ofDays(jwtConfig.getRefreshToken().getExpiration()))
                .build();
    }
}
