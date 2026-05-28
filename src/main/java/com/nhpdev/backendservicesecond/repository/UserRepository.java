package com.nhpdev.backendservicesecond.repository;

import com.nhpdev.backendservicesecond.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    boolean existsUsersByEmail(@NotBlank(message = "email is required") String email);
    boolean existsUsersByDisplayName(String displayName);
}
