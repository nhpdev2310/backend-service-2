package com.nhpdev.backendservicesecond.service;

import com.nhpdev.backendservicesecond.dto.request.UserCreateRequest;
import com.nhpdev.backendservicesecond.dto.request.PaginationRequest;
import com.nhpdev.backendservicesecond.dto.request.UserStatusRequest;
import com.nhpdev.backendservicesecond.dto.request.UserUpdateOwnRequest;
import com.nhpdev.backendservicesecond.dto.response.PageResponse;
import com.nhpdev.backendservicesecond.dto.response.UserDetailResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserDetailResponse createUser(UserCreateRequest request);
    PageResponse<UserDetailResponse> getAllUser(PaginationRequest pageRequest,
                                                String email, String displayName);
    UserDetailResponse getUserById(String userId);
    UserDetailResponse myInfo(String userId);
    UserDetailResponse updateMyInfo(String userId, UserUpdateOwnRequest request);
    UserDetailResponse updateUserStatus(String userId, UserStatusRequest request);
    void verfifyAccount(String verifyToken);
    void uploadAvatar(String userId, MultipartFile image);
    String getMyAvatar(String userId);
    String getUserAvatar(String userId);
}
