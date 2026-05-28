package com.nhpdev.backendservicesecond.service.impl;

import com.nhpdev.backendservicesecond.common.nhpEnum.UserStatus;
import com.nhpdev.backendservicesecond.dto.request.PaginationRequest;
import com.nhpdev.backendservicesecond.dto.request.UserCreateRequest;
import com.nhpdev.backendservicesecond.dto.response.PageResponse;
import com.nhpdev.backendservicesecond.dto.response.UserDetailResponse;
import com.nhpdev.backendservicesecond.entity.User;
import com.nhpdev.backendservicesecond.repository.UserRepository;
import com.nhpdev.backendservicesecond.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetailResponse createUser(UserCreateRequest request) {
        if (userRepository.existsUsersByEmail(request.email()))
            throw new RuntimeException("Email is already exist");
        if(userRepository.existsUsersByDisplayName(request.displayName()))
            throw new RuntimeException("DisplayName is already exist");
        User user = User.builder()
                .email(request.email())
                .displayName(request.displayName())
                .password(request.password())
                .status(UserStatus.INACTIVE)
                .build();
        User savedUser = userRepository.save(user);
        return UserDetailResponse.of(savedUser);
    }

    @Override
    public PageResponse<UserDetailResponse> getAllUser(PaginationRequest pageRequest, String email, String displayName, UserStatus status) {
        return null;
    }

    @Override
    public UserDetailResponse getUserById(String userId) {
        return null;
    }
}
