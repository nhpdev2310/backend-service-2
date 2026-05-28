package com.nhpdev.backendservicesecond.controller;

import com.nhpdev.backendservicesecond.common.constraint.AppConstants;
import com.nhpdev.backendservicesecond.dto.request.PaginationRequest;
import com.nhpdev.backendservicesecond.dto.request.UserCreateRequest;
import com.nhpdev.backendservicesecond.dto.response.ApiResponse;
import com.nhpdev.backendservicesecond.dto.response.PageResponse;
import com.nhpdev.backendservicesecond.dto.response.UserDetailResponse;
import com.nhpdev.backendservicesecond.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AppConstants.URL_PREFIX + "/users")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ApiResponse<UserDetailResponse> createUser(@Valid  @RequestBody UserCreateRequest request) {
        return ApiResponse.created(userService.createUser(request));
    }

    @GetMapping("")
    public ApiResponse<PageResponse<UserDetailResponse>> getAllUser(
            @ModelAttribute @Valid PaginationRequest pageRequest,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String displayName
    ) {
        return ApiResponse.success(userService.getAllUser(pageRequest, email, displayName));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDetailResponse> getUserById(@PathVariable("id") String userId) {
        return ApiResponse.success(userService.getUserById(userId));
    }
}
