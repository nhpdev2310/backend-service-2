package com.nhpdev.backendservicesecond.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"user", "role"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "user_has_role",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_role",
                        columnNames = {"user_id", "role_id"}
                )
        }
)
public class UserHasRole {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_uhr_user")
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "role_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_uhr_role")
    )
    private Role role;
}
