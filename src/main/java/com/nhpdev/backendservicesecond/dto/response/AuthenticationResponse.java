package com.nhpdev.backendservicesecond.dto.response;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String accessToken,
        String refreshToken
) {
}
