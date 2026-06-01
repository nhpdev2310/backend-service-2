package com.nhpdev.backendservicesecond.dto.response;

import lombok.*;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private String displayName;
    private Collection<String> roles;

    public static LoginResponse of (AuthenticationResponse response) {
        return LoginResponse.builder()
                .accessToken(response.getAccessToken())
                .displayName(response.getDisplayName())
                .roles(response.getRoles())
                .build();
    }

}
