package com.algofusion.businesscard.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.algofusion.businesscard.entities.User;

public class UserSpecifications {
    public static Specification<User> nameContains(String keyword) {
        return (root, query, cb) -> keyword == null || keyword.isBlank() ? null
                : cb.like(cb.lower(root.get("username")), "%" + keyword.toLowerCase() + "%");
    }
}
