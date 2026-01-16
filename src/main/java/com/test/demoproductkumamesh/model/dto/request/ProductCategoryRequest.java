package com.test.demoproductkumamesh.model.dto.request;

import com.test.demoproductkumamesh.model.entity.ProductCategory;
import jakarta.validation.constraints.NotBlank;

public record ProductCategoryRequest (
        @NotBlank(message = "name is required")
        String name
) {
        public ProductCategory toEntity() {
                return ProductCategory.builder()
                        .name(name)
                        .build();
        }
}
