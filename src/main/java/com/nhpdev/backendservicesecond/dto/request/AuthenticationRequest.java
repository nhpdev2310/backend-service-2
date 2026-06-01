package com.nhpdev.backendservicesecond.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.nhpdev.backendservicesecond.constraint.ValidationMessage.*;

public record AuthenticationRequest(
        @NotBlank(message = MISSING_FIELD)
        @Email(message = INVALID_EMAIL)
        String email,

        @NotBlank(message = MISSING_FIELD)
        @Size(min = 5, message = INVALID_PASSWORD)
        String password
) {
}
