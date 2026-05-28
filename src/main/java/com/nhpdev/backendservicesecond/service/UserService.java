package com.nhpdev.backendservicesecond.service;

import com.nhpdev.backendservicesecond.dto.request.UserCreateRequest;
import com.nhpdev.backendservicesecond.dto.request.PaginationRequest;
import com.nhpdev.backendservicesecond.dto.response.PageResponse;
import com.nhpdev.backendservicesecond.dto.response.UserDetailResponse;

public interface UserService {
    UserDetailResponse createUser(UserCreateRequest request);
    PageResponse<UserDetailResponse> getAllUser(PaginationRequest pageRequest,
                                                String email, String displayName);
    UserDetailResponse getUserById(String userId);
}
