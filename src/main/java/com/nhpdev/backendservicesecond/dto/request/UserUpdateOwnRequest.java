package com.nhpdev.backendservicesecond.dto.request;

import jakarta.validation.constraints.Size;

import static com.nhpdev.backendservicesecond.constraint.ValidationMessage.*;

public record UserUpdateOwnRequest(
        @Size(min = 3, message = INVALID_INPUT)
        String displayName,
        String bio
) { }
