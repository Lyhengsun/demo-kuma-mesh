package com.test.demoproductkumamesh.controller;

import com.test.demoproductkumamesh.model.dto.request.IdListRequest;
import com.test.demoproductkumamesh.model.dto.request.ProductCategoryRequest;
import com.test.demoproductkumamesh.model.dto.request.ProductRequest;
import com.test.demoproductkumamesh.model.dto.response.ApiResponse;
import com.test.demoproductkumamesh.model.dto.response.IdStatusResponse;
import com.test.demoproductkumamesh.model.dto.response.MultiStatusResponse;
import com.test.demoproductkumamesh.model.dto.response.PagedResponse;
import com.test.demoproductkumamesh.model.entity.Product;
import com.test.demoproductkumamesh.model.entity.ProductCategory;
import com.test.demoproductkumamesh.model.enumeration.ProductAvailability;
import com.test.demoproductkumamesh.model.enumeration.ProductProperty;
import com.test.demoproductkumamesh.service.ProductService;
import com.test.demoproductkumamesh.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Controller", description = "Controller to manage products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Product>>> getAllProducts(
            @RequestParam(defaultValue = "1")
            @Positive
            Integer page,
            @RequestParam(defaultValue = "10")
            @Positive
            Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) ProductAvailability availability,
            @RequestParam(required = false) BigDecimal minUnitPrice,
            @RequestParam(required = false) BigDecimal maxUnitPrice,
            @RequestParam(required = false) @Positive @Max(value = Long.MAX_VALUE) Long minAmount,
            @RequestParam(required = false) Long maxAmount,
            @RequestParam(defaultValue = "CREATED_AT") ProductProperty productProperty,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        return ResponseUtils.createResponse("Fetch products successfully", productService.getAllProducts(
                page, size, name, categoryId, availability,  minUnitPrice, maxUnitPrice, minAmount, maxAmount, productProperty, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable UUID id) {
        return ResponseUtils.createResponse("Fetch product by id successfully", productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> addProduct(@RequestBody ProductRequest request) {
        return ResponseUtils.createResponse("Create new product successfully", productService.addProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProductById(@PathVariable UUID id, @RequestBody ProductRequest request) {
        return ResponseUtils.createResponse("Update product with ID: " + id.toString() + " successfully", productService.updateProductById(id, request));
    }

    @PostMapping("/bulk-delete")
    public ResponseEntity<ApiResponse<MultiStatusResponse<IdStatusResponse>>> bulkDeleteProductByIdList(@RequestBody IdListRequest request) {
        MultiStatusResponse<IdStatusResponse> response = productService.bulkDeleteProductByIdList(request);
        if (response.getFailures().isEmpty())
            return ResponseUtils.createResponse("Successfully delete products", response);
        else
            return ResponseUtils.createResponse("Deleted products", HttpStatus.MULTI_STATUS, response);
    }

}
