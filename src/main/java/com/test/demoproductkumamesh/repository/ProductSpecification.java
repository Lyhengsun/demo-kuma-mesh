package com.test.demoproductkumamesh.repository;

import com.test.demoproductkumamesh.model.entity.Product;
import com.test.demoproductkumamesh.model.enumeration.ProductAvailability;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductSpecification {
    public static Specification<Product> productNameContains(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasCategoryId(UUID categoryId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> lessThanOrEqualUnitPrice(BigDecimal unitPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("unitPrice"), unitPrice);
    }

    public static Specification<Product> greaterThanOrEqualUnitPrice(BigDecimal unitPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("unitPrice"), unitPrice);
    }

    public static Specification<Product> lessThanOrEqualAmount(Long amount) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("amount"), amount);
    }

    public static Specification<Product> greaterThanOrEqualAmount(Long amount) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), amount);
    }

    public static Specification<Product> hasAvailability(ProductAvailability availability) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("availability"), availability);
    }
}
