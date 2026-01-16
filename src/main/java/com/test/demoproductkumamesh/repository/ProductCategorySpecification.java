package com.test.demoproductkumamesh.repository;

import com.test.demoproductkumamesh.model.entity.ProductCategory;
import org.springframework.data.jpa.domain.Specification;

public class ProductCategorySpecification {
    public static Specification<ProductCategory> categoryNameContains(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name + "%");
    }
}
