package com.nhpdev.backendservicesecond.service.impl;

import com.nhpdev.backendservicesecond.entity.Role;
import com.nhpdev.backendservicesecond.repository.RoleRepository;
import com.nhpdev.backendservicesecond.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public List<Role> getAllPermissionByRoleNames(Collection<String> roleNames) {
        return roleRepository.getAllPermissionByRoleNames(roleNames);
    }
}
