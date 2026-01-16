package com.test.demoproductkumamesh.model.entity;

import com.test.demoproductkumamesh.model.enumeration.ProductAvailability;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ProductCategory category;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductAvailability availability;

    @Column(columnDefinition = "TEXT")
    private String description;

    public void update(Product product) {
        setName(product.getName());
        setCategory(product.getCategory());
        setUnitPrice(product.getUnitPrice());
        setAmount(product.getAmount());
        setAvailability(product.getAvailability());
        setDescription(product.getDescription());
    }
}
