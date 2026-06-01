package com.nhpdev.backendservicesecond.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nhpdev.backendservicesecond.entity.Permission;
import com.nhpdev.backendservicesecond.entity.Role;
import com.nhpdev.backendservicesecond.entity.RolePermission;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleDetailResponse {
    private String id;
    private String name;
    private List<String> permissions;
    private String description;

    public static RoleDetailResponse of(Role role) {
        return RoleDetailResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .permissions(role.getRolePermissions().stream()
                        .map(RolePermission::getPermission)
                        .map(Permission::getName).toList())
                .build();
    }

    public static List<RoleDetailResponse> ofAll(List<Role> roles) {
        return roles.stream().map(RoleDetailResponse::of).toList();
    }
}
