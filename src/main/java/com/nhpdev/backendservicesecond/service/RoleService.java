package com.nhpdev.backendservicesecond.service;


import com.nhpdev.backendservicesecond.dto.response.RoleDetailResponse;

import java.util.Collection;
import java.util.List;

public interface RoleService {
    List<RoleDetailResponse> getAllPermissionByRoleNames(Collection<String> roleNames);
}
