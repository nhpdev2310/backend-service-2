package com.nhpdev.backendservicesecond.repository;

import com.nhpdev.backendservicesecond.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    boolean existsUsersByEmail(@NotBlank(message = "email is required") String email);
    boolean existsUsersByDisplayName(String displayName);
    Optional<User> getUserByEmail(String email);
}
