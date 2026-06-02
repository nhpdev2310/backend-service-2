package com.nhpdev.backendservicesecond.dto.request;

import com.nhpdev.backendservicesecond.common.nhpenum.UserStatus;

public record UserStatusRequest(
        UserStatus status,
        Boolean isBanned
) { }
