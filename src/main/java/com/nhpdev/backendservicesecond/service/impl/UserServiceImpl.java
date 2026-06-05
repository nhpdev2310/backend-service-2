package com.nhpdev.backendservicesecond.service.impl;

import com.nhpdev.backendservicesecond.common.PermissionCode;
import com.nhpdev.backendservicesecond.common.nhpenum.TokenType;
import com.nhpdev.backendservicesecond.common.nhpenum.UserStatus;
import static com.nhpdev.backendservicesecond.constraint.MailConstants.*;

import com.nhpdev.backendservicesecond.constraint.AppConstants;
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
import com.nhpdev.backendservicesecond.service.JwtService;
import com.nhpdev.backendservicesecond.service.MailService;
import com.nhpdev.backendservicesecond.service.TokenService;
import com.nhpdev.backendservicesecond.service.UserService;
import com.nimbusds.jwt.SignedJWT;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.util.StringUtils;

import java.text.ParseException;

import static com.nhpdev.backendservicesecond.constraint.RedisConstant.USER_DETAIL_LIST_CACHE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j(topic = "USER_SERVICE")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final TokenService tokenService;

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
        String verificationLink = createVerificationLink(savedUser);
        mailService.sendVerificationEmail(request.email(),
                VERIFICATION_SUBJECT, request.displayName(), VERIFICATION_TEMP, verificationLink);
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

    @Override
    @Transactional
    @CacheEvict(value = USER_DETAIL_LIST_CACHE, allEntries = true)
    public void verfifyAccount(String verifyToken) {
        String userId = tokenService.validateAndConsumeVerificationToken(verifyToken);
        if(userId == null) {
            throw new BackendServiceException(ErrorCode.TOKEN_EXPIRED);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BackendServiceException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(UserStatus.ACTIVE);
    }

    private String createVerificationLink(@NonNull User user) {
        if (user.isEnabled()) {
            log.warn("User {} is already activated. Aborting link generation.", user.getEmail());
            throw new BackendServiceException(ErrorCode.USER_ALREADY_ACTIVATED);
        }
        String token = tokenService.createVerificationToken(user.getId());
        if (!StringUtils.hasText(token)) {
            log.warn("Verification token is null, user: {}", user.getEmail());
            throw new BackendServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return AppConstants.HOST +
                AppConstants.URL_PREFIX +
                "/users/verification/" +
                token;
    }
}
