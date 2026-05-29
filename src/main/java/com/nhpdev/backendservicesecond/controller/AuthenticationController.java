package com.nhpdev.backendservicesecond.controller;

import com.nhpdev.backendservicesecond.common.constraint.AppConstants;
import com.nhpdev.backendservicesecond.dto.request.AuthenticationRequest;
import com.nhpdev.backendservicesecond.dto.response.ApiResponse;
import com.nhpdev.backendservicesecond.dto.response.AuthenticationResponse;
import com.nhpdev.backendservicesecond.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping(AppConstants.URL_PREFIX + "/auth")
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ApiResponse.success(authService.authenticate(request));
    }

    @PostMapping("/refresh/{refreshToken}")
    public ApiResponse<AuthenticationResponse> refreshToken(@PathVariable String refreshToken) {
        return ApiResponse.success(authService.refreshToken(refreshToken));
    }
}
