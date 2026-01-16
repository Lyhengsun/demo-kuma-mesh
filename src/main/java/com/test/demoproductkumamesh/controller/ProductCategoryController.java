package com.test.demoproductkumamesh.controller;

import com.test.demoproductkumamesh.model.dto.request.ProductCategoryRequest;
import com.test.demoproductkumamesh.model.dto.response.ApiResponse;
import com.test.demoproductkumamesh.model.dto.response.PagedResponse;
import com.test.demoproductkumamesh.model.entity.ProductCategory;
import com.test.demoproductkumamesh.service.ProductService;
import com.test.demoproductkumamesh.utils.ResponseUtils;
import com.test.demoproductkumamesh.validation.annotation.ValidPositiveIntParam;
import com.test.demoproductkumamesh.validation.annotation.ValidStringParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/product-categories")
@RequiredArgsConstructor
@Tag(name = "Product Category Controller", description = "Controller to manage product categories")
public class ProductCategoryController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProductCategory>>> getAllProductCategories(
            @Valid
            @RequestParam(defaultValue = "1", required = true)
            @ValidPositiveIntParam(fieldName = "page")
            Long page,
            @Valid
            @RequestParam(defaultValue = "10", required = true)
            @ValidPositiveIntParam(fieldName = "size")
            Long size,
            @Valid
            @RequestParam(required = false)
            @ValidStringParam(fieldName = "name", max = 100)
            String name,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        return ResponseUtils.createResponse("Fetch product categories successfully", productService.getAllProductCategories(
                page.intValue(), size.intValue(), name, direction
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategory>> getProductCategoryById(@PathVariable UUID id) {
        return ResponseUtils.createResponse("Fetch product category by id successfully", productService.getProductCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductCategory>> addProductCategory(@RequestBody ProductCategoryRequest request) {
        return ResponseUtils.createResponse("Create new category successfully", productService.addProductCategory(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategory>> updateProductCategory(@PathVariable UUID id, @RequestBody ProductCategoryRequest request) {
        return ResponseUtils.createResponse("Update product category with ID: " + id.toString() + " successfully", productService.updateProductCategoryById(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProductCategory(@PathVariable UUID id) {
        productService.deleteProductCategoryById(id);
        return ResponseUtils.createResponse("Delete product category with ID: " + id.toString() + " successfully");
    }
}
