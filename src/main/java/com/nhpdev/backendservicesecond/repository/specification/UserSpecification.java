package com.nhpdev.backendservicesecond.repository.specification;

import com.nhpdev.backendservicesecond.entity.User;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static PredicateSpecification<User> hasEmail(String email) {
        return (from, builder) ->
            StringUtils.isBlank(email) ? null
            : builder.like(builder.lower(from.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> hasDisplayName(String displayName) {
        return (root, query, criteriaBuilder) ->
    }
}
