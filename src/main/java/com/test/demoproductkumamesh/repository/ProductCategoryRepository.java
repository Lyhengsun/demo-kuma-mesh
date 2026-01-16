package com.test.demoproductkumamesh.repository;

import com.test.demoproductkumamesh.model.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID>, JpaSpecificationExecutor<ProductCategory> {
}
