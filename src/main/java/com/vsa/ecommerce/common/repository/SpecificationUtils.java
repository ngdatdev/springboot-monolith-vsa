package com.vsa.ecommerce.common.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

/**
 * Utility for creating JPA Class Specifications.
 */
public class SpecificationUtils {

    private SpecificationUtils() {
        // Private constructor
    }

    public static <T> Specification<T> distinct() {
        return (root, query, cb) -> {
            query.distinct(true);
            return null;
        };
    }

    public static <T> Specification<T> equal(String attribute, Object value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }
            return cb.equal(root.get(attribute), value);
        };
    }

    public static <T> Specification<T> like(String attribute, String value) {
        return (root, query, cb) -> {
            if (value == null || value.isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get(attribute)), "%" + value.toLowerCase() + "%");
        };
    }

    public static <T> Specification<T> in(String attribute, Collection<?> values) {
        return (root, query, cb) -> {
            if (values == null || values.isEmpty()) {
                return null;
            }
            return root.get(attribute).in(values);
        };
    }

    public static <T> Specification<T> greaterThan(String attribute, Comparable value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }
            return cb.greaterThan(root.get(attribute), value);
        };
    }

    public static <T> Specification<T> lessThan(String attribute, Comparable value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }
            return cb.lessThan(root.get(attribute), value);
        };
    }

    public static <T> Specification<T> isTrue(String attribute) {
        return (root, query, cb) -> cb.isTrue(root.get(attribute));
    }

    public static <T> Specification<T> isFalse(String attribute) {
        return (root, query, cb) -> cb.isFalse(root.get(attribute));
    }
}
