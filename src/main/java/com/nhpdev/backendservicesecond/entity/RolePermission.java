package com.nhpdev.backendservicesecond.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"role", "permission"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "role_permission",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_role_permission",
                columnNames = {"role_id", "permission_id"}
        )
)
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "role_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_rp_role")
    )
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "permission_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_rp_permission")
    )
    private Permission permission;
}
