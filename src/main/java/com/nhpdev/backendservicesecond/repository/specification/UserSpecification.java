package com.nhpdev.backendservicesecond.repository.specification;

import com.nhpdev.backendservicesecond.common.nhpenum.UserStatus;
import com.nhpdev.backendservicesecond.entity.User;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.jpa.domain.PredicateSpecification;

public class UserSpecification {
    private UserSpecification() {
        throw new UnsupportedOperationException("Utility class");
    }
    public static PredicateSpecification<User> hasEmail(String email) {
        return (from, cb) ->
            StringUtils.isBlank(email) ? null
            : cb.like(cb.lower(from.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static PredicateSpecification<User> hasDisplayName(String displayName) {
        return (from, cb) -> StringUtils.isBlank(displayName) ? null
                : cb.like(cb.lower(from.get("displayName")), "%" + displayName.toLowerCase() + "%");
    }

    public static PredicateSpecification<User> hasStatus(UserStatus status) {
        return (from, cb) -> status == null ? null
            : cb.equal(from.get("status"), status);
    }
}
