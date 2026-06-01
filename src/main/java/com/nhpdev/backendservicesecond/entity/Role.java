package com.nhpdev.backendservicesecond.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "roles")
public class Role extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePermission> rolePermissions = new HashSet<>();

    public void addPermission(Permission permission) {
        RolePermission rolePermission = RolePermission.builder()
                .role(this)
                .permission(permission)
                .build();
        this.rolePermissions.add(rolePermission);
    }

    /***
     *
     * @param newPermissions: Set of new list of permission
     */
    public void syncPermissions(Set<Permission> newPermissions) {
        if (this.rolePermissions == null) {
            this.rolePermissions = new HashSet<>();
        }
        // 1. Xóa những quyền hiện tại KHÔNG CÓ trong danh sách mới (YAML)
        this.rolePermissions.removeIf(rp -> !newPermissions.contains(rp.getPermission()));
        // 2. Gom danh sách các quyền đang có thành 1 Set để kiểm tra
        Set<Permission> currentPermissions = this.rolePermissions.stream()
                .map(RolePermission::getPermission)
                .collect(Collectors.toSet());
        // 3. Thêm những quyền MỚI vào
        for (Permission permission : newPermissions) {
            if (!currentPermissions.contains(permission)) {
                this.addPermission(permission);
            }
        }
    }
}
