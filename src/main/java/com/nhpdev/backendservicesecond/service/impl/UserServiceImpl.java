package com.nhpdev.backendservicesecond.service.impl;

import com.nhpdev.backendservicesecond.common.nhpenum.UserStatus;
import com.nhpdev.backendservicesecond.dto.request.PaginationRequest;
import com.nhpdev.backendservicesecond.dto.request.UserCreateRequest;
import com.nhpdev.backendservicesecond.dto.response.PageResponse;
import com.nhpdev.backendservicesecond.dto.response.UserDetailResponse;
import com.nhpdev.backendservicesecond.entity.User;
import com.nhpdev.backendservicesecond.repository.UserRepository;
import com.nhpdev.backendservicesecond.repository.specification.UserSpecification;
import com.nhpdev.backendservicesecond.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.PredicateSpecification;
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
    public PageResponse<UserDetailResponse> getAllUser(PaginationRequest pageRequest, String email, String displayName) {
        Pageable pageable = PageRequest.of(
                pageRequest.getPageNumber() - 1,
                pageRequest.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        PredicateSpecification<User> spec = PredicateSpecification.allOf(
            UserSpecification.hasEmail(email),
            UserSpecification.hasDisplayName(displayName)
        );

        Page<UserDetailResponse> userPage = userRepository.findBy(
                spec,
                q -> q.page(pageable))
                .map(UserDetailResponse::of);
        return PageResponse.of(userPage);
    }

    @Override
    public UserDetailResponse getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User is not exist!"));
        return UserDetailResponse.of(user);
    }
}
