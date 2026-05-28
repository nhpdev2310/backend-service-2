package com.nhpdev.backendservicesecond.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/***
 *
 * @param email - Email format
 * @param displayName - optional
 * @param password - Min 8  characters
 */
@Builder
public record UserCreateRequest(
        @NotBlank(message = "email is required")
        @Email
        String email,
        String displayName,
        @NotBlank(message = "password is required")
        @Size(min = 5, message = "password must have at least 5 characters")
        String password
) {
}
