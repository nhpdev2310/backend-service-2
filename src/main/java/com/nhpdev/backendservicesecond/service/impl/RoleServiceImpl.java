package com.nhpdev.backendservicesecond.service.impl;

import static com.nhpdev.backendservicesecond.constraint.RedisConstant.ROLE_PERMISSIONS_CACHE;
import com.nhpdev.backendservicesecond.dto.response.RoleDetailResponse;
import com.nhpdev.backendservicesecond.repository.RoleRepository;
import com.nhpdev.backendservicesecond.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    @Cacheable(value = ROLE_PERMISSIONS_CACHE, key = "#roleNames")
    public List<RoleDetailResponse> getAllPermissionByRoleNames(Collection<String> roleNames) {
        return RoleDetailResponse.ofAll(roleRepository.getAllPermissionByRoleNames(roleNames));
    }
}
