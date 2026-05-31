package com.nhpdev.backendservicesecond.entity;

import com.nhpdev.backendservicesecond.common.nhpenum.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

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
    @Column(nullable = false, length = 25)
    private UserStatus status = UserStatus.INACTIVE;

    @Column(length = 100)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private boolean isBanned = false;

    @Builder.Default
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)

    private transient Set<UserHasRole> userHasRoles = new HashSet<>();

    public void addRole(Role role) {
        UserHasRole userHasRole = UserHasRole.builder()
                .user(this)
                .role(role)
                .build();
        this.userHasRoles.add(userHasRole);
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return userHasRoles.stream()
                .map(uhr ->
                        new SimpleGrantedAuthority(uhr.getRole().getName())).toList();
    }

    @Override
    public @NonNull String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.getStatus().equals(UserStatus.ACTIVE) && !this.isBanned;
    }
}
