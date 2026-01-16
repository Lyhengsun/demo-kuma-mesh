package com.test.demoproductkumamesh.model.dto.request;

import com.test.demoproductkumamesh.model.entity.Product;
import com.test.demoproductkumamesh.model.entity.ProductCategory;
import com.test.demoproductkumamesh.model.enumeration.ProductAvailability;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequest (
        @NotBlank(message = "name is required")
        @Size(min = 3, max = 100, message = "name need to be between 3-100 letters")
        String name,

        @NotBlank(message = "category is required")
        UUID categoryId,

        @NotNull(message = "unit price is required")
        BigDecimal unitPrice,

        @NotNull(message = "amount is required")
        Long amount,

        @NotNull(message = "Product Availability is required. Acceptable Value are 'IN_STOCK' 'OUT_OF_STOCK' 'UNAVAILABLE'")
        ProductAvailability availability,

        String description
) {
        public Product toEntity(ProductCategory category) {
                return Product.builder()
                        .name(name)
                        .category(category)
                        .unitPrice(unitPrice)
                        .amount(amount)
                        .availability(availability)
                        .description(description)
                        .build();
        }
}
