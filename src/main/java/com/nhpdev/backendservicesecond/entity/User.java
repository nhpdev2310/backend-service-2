package com.nhpdev.backendservicesecond.entity;

import com.nhpdev.backendservicesecond.common.nhpenum.UserStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "display_name", nullable = false, unique = true)
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private UserStatus status = UserStatus.INACTIVE;

    private String password;
}
