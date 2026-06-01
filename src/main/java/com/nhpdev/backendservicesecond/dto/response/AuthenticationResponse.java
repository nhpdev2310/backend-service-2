package com.nhpdev.backendservicesecond.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponse{
    private String accessToken;
    private String refreshToken;
    private String displayName;
    private Collection<String> roles;
}
