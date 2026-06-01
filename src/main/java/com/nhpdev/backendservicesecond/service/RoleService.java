package com.nhpdev.backendservicesecond.service;


import com.nhpdev.backendservicesecond.entity.Role;

import java.util.Collection;
import java.util.List;

public interface RoleService {
    List<Role> getAllPermissionByRoleNames(Collection<String> roleNames);
}
