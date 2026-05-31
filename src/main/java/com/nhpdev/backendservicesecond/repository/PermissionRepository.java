package com.nhpdev.backendservicesecond.repository;

import com.nhpdev.backendservicesecond.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, String> {
}
