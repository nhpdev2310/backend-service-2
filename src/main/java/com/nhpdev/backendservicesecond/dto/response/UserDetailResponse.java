package com.nhpdev.backendservicesecond.dto.response;

import com.nhpdev.backendservicesecond.common.nhpenum.UserStatus;
import com.nhpdev.backendservicesecond.entity.User;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UserDetailResponse(
        String id,
        String displayName,
        String email,
        String bio,
        UserStatus status,
        Instant createdAt
) {
    public static UserDetailResponse of(User user) {
        return UserDetailResponse.builder()
                .id(user.getId())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .bio(user.getBio())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
