package com.nhpdev.backendservicesecond.repository;

import com.nhpdev.backendservicesecond.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface RoleRepository extends JpaRepository<Role, String> {

    @Query("""
            SELECT r FROM Role r 
            LEFT JOIN FETCH r.rolePermissions rp 
            LEFT JOIN FETCH rp.permission p 
                    WHERE r.name IN :roleNames
        """)
    List<Role> getAllPermissionByRoleNames(@Param("roleNames") Collection<String> roleNames);
}
