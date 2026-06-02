package com.nhpdev.backendservicesecond.service.impl;

import com.nhpdev.backendservicesecond.common.PermissionCode;
import com.nhpdev.backendservicesecond.common.nhpenum.UserStatus;
import com.nhpdev.backendservicesecond.dto.request.PaginationRequest;
import com.nhpdev.backendservicesecond.dto.request.UserCreateRequest;
import com.nhpdev.backendservicesecond.dto.request.UserStatusRequest;
import com.nhpdev.backendservicesecond.dto.request.UserUpdateOwnRequest;
import com.nhpdev.backendservicesecond.dto.response.PageResponse;
import com.nhpdev.backendservicesecond.dto.response.UserDetailResponse;
import com.nhpdev.backendservicesecond.entity.User;
import com.nhpdev.backendservicesecond.exception.BackendServiceException;
import com.nhpdev.backendservicesecond.exception.ErrorCode;
import com.nhpdev.backendservicesecond.repository.UserRepository;
import com.nhpdev.backendservicesecond.repository.specification.UserSpecification;
import com.nhpdev.backendservicesecond.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.nhpdev.backendservicesecond.constraint.RedisConstant.USER_DETAIL_LIST_CACHE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    @CacheEvict(value = USER_DETAIL_LIST_CACHE, allEntries = true)
    public UserDetailResponse createUser(UserCreateRequest request) {
        if (userRepository.existsUsersByEmail(request.email()))
            throw new BackendServiceException(ErrorCode.USER_ALREADY_EXISTS);
        if(userRepository.existsUsersByDisplayName(request.displayName()))
            throw new BackendServiceException(ErrorCode.USER_ALREADY_EXISTS);
        User user = User.builder()
                .email(request.email())
                .displayName(request.displayName())
                .password(passwordEncoder.encode(request.password()))
                .status(UserStatus.INACTIVE)
                .build();
        User savedUser = userRepository.save(user);
        return UserDetailResponse.of(savedUser);
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.USER_READ_ANY + "')")
    @Cacheable(value = USER_DETAIL_LIST_CACHE, key = "'page:' + #pageRequest.pageNumber + 'size:' + #pageRequest.pageSize + ':q:' + #email + ':' + #displayName")
    public PageResponse<UserDetailResponse> getAllUser(PaginationRequest pageRequest,
                                                       String email, String displayName) {
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
    @PreAuthorize("hasAuthority('" + PermissionCode.USER_READ_ANY + "')")
    public UserDetailResponse getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BackendServiceException(ErrorCode.USER_NOT_FOUND));
        return UserDetailResponse.of(user);
    }

    @Override
    @PreAuthorize("isAuthenticated() and #userId == principal.subject")
    public UserDetailResponse myInfo(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BackendServiceException(ErrorCode.USER_NOT_FOUND));
        return UserDetailResponse.of(user);
    }

    @Override
    @PreAuthorize("isAuthenticated() and #userId == principal.subject")
    @Transactional
    @CacheEvict(value = USER_DETAIL_LIST_CACHE, allEntries = true)
    public UserDetailResponse updateMyInfo(String userId, UserUpdateOwnRequest request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BackendServiceException(ErrorCode.USER_NOT_FOUND));
        if(request.displayName() != null) user.setDisplayName(request.displayName());
        if(request.bio() != null) user.setBio(request.bio());
        return UserDetailResponse.of(user);
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.USER_UPDATE_STATUS + "')")
    @Transactional
    @CacheEvict(value = USER_DETAIL_LIST_CACHE, allEntries = true)
    public UserDetailResponse updateUserStatus(String userId, UserStatusRequest request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BackendServiceException(ErrorCode.USER_NOT_FOUND));
        if(request.isBanned() != null) user.setBanned(request.isBanned());
        if(request.status() != null) user.setStatus(request.status());
        return UserDetailResponse.of(user);
    }
}
