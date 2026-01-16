package com.test.demoproductkumamesh.service;

import com.test.demoproductkumamesh.exception.BadRequestException;
import com.test.demoproductkumamesh.exception.NotFoundException;
import com.test.demoproductkumamesh.model.dto.request.IdListRequest;
import com.test.demoproductkumamesh.model.dto.request.ProductCategoryRequest;
import com.test.demoproductkumamesh.model.dto.request.ProductRequest;
import com.test.demoproductkumamesh.model.dto.response.IdStatusResponse;
import com.test.demoproductkumamesh.model.dto.response.MultiStatusResponse;
import com.test.demoproductkumamesh.model.dto.response.PagedResponse;
import com.test.demoproductkumamesh.model.dto.response.PaginationInfo;
import com.test.demoproductkumamesh.model.entity.Product;
import com.test.demoproductkumamesh.model.entity.ProductCategory;
import com.test.demoproductkumamesh.model.enumeration.ProductAvailability;
import com.test.demoproductkumamesh.model.enumeration.ProductProperty;
import com.test.demoproductkumamesh.repository.ProductCategoryRepository;
import com.test.demoproductkumamesh.repository.ProductCategorySpecification;
import com.test.demoproductkumamesh.repository.ProductRepository;
import com.test.demoproductkumamesh.repository.ProductSpecification;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public PagedResponse<Product> getAllProducts(
            @Positive Integer page,
            @Positive Integer size,
            String name,
            UUID categoryId,
            ProductAvailability availability,
            BigDecimal minUnitPrice,
            BigDecimal maxUnitPrice,
            Long minAmount,
            Long maxAmount,
            ProductProperty productProperty,
            Sort.Direction direction
    ) {
        if (categoryId != null) {
            getProductCategoryById(categoryId);
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, productProperty.getValue()));

        Specification<Product> spec = Specification.unrestricted();
        if (name != null && !name.isEmpty())
            spec = spec.and(ProductSpecification.productNameContains(name));

        if (categoryId != null)
            spec = spec.and(ProductSpecification.hasCategoryId(categoryId));

        if (availability != null)
            spec = spec.and(ProductSpecification.hasAvailability(availability));

        if (minUnitPrice != null)
            spec = spec.and(ProductSpecification.greaterThanOrEqualUnitPrice(minUnitPrice));

        if (maxUnitPrice != null)
            spec = spec.and(ProductSpecification.lessThanOrEqualUnitPrice(maxUnitPrice));

        if (minAmount != null)
            spec = spec.and(ProductSpecification.greaterThanOrEqualAmount(minAmount));

        if (maxAmount != null)
            spec = spec.and(ProductSpecification.lessThanOrEqualAmount(maxAmount));

        Page<Product> products = productRepository.findAll(spec, pageable);

        return PagedResponse.<Product>builder()
                .items(products.getContent())
                .pagination(new PaginationInfo(products))
                .build();
    }

    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public Product addProduct(ProductRequest request) {
        ProductCategory foundProductCategory = getProductCategoryById(request.categoryId());
        validateProductRequest(request);
        return productRepository.save(request.toEntity(foundProductCategory));
    }

    public Product updateProductById(UUID id, ProductRequest request) {
        ProductCategory foundProductCategory = getProductCategoryById(request.categoryId());
        Product foundProduct = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        validateProductRequest(request);

        foundProduct.update(request.toEntity(foundProductCategory));
        productRepository.save(foundProduct);
        return foundProduct;
    }

    public MultiStatusResponse<IdStatusResponse> bulkDeleteProductByIdList(IdListRequest request) {
        List<IdStatusResponse> successes = new ArrayList<>();
        List<IdStatusResponse> failures = new ArrayList<>();

        request.ids().forEach((id) -> {
            try {
                Product product = getProductById(id);
                productRepository.delete(product);
                successes.add(IdStatusResponse.builder().id(id).status(HttpStatus.OK.value()).detail("Deleted this product successfully").build());
            } catch (NotFoundException e) {
                failures.add(IdStatusResponse.builder().id(id).status(HttpStatus.NOT_FOUND.value()).detail(e.getMessage()).build());
            } catch (BadRequestException e) {
                failures.add(IdStatusResponse.builder().id(id).status(HttpStatus.BAD_REQUEST.value()).detail(e.getMessage()).build());
            } catch (Exception e) {
                failures.add(IdStatusResponse.builder().id(id).status(HttpStatus.INTERNAL_SERVER_ERROR.value()).detail(e.getMessage()).build());
            }
        });

        return MultiStatusResponse.<IdStatusResponse>builder()
                .successes(successes)
                .failures(failures)
                .build();
    }

    public PagedResponse<ProductCategory> getAllProductCategories(@Positive Integer page, @Positive Integer size, String name, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, "name"));

        Specification<ProductCategory> spec = Specification.unrestricted();
        if (name != null && !name.isEmpty())
            spec = spec.and(ProductCategorySpecification.categoryNameContains(name));

        Page<ProductCategory> productCategories = productCategoryRepository.findAll(spec, pageable);
        return PagedResponse.<ProductCategory>builder()
                .items(productCategories.getContent())
                .pagination(new PaginationInfo(productCategories))
                .build();
    }

    public ProductCategory getProductCategoryById(UUID id) {
        return productCategoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
    }

    public ProductCategory addProductCategory(ProductCategoryRequest request) {
        return productCategoryRepository.save(request.toEntity());
    }

    public ProductCategory updateProductCategoryById(UUID id, ProductCategoryRequest request) {
        ProductCategory foundProductCategory = getProductCategoryById(id);
        foundProductCategory.setName(request.name());
        productCategoryRepository.save(foundProductCategory);
        return foundProductCategory;
    }

    public void deleteProductCategoryById(UUID id) {
        ProductCategory foundProductCategory = getProductCategoryById(id);
        int productCount = productRepository.countByCategoryId(foundProductCategory.getId());
        if (productCount > 0) {
            throw new BadRequestException("This category is being used by " + productCount + " product" +  (productCount > 1 ? "s" : "") + " and can't be deleted");
        }

        productCategoryRepository.delete(foundProductCategory);
    }

    private void validateProductRequest(ProductRequest request) {
        if (!(request.amount() > 0) && request.availability() == ProductAvailability.IN_STOCK ) {
            throw new BadRequestException("Amount of 0 can't be in stock");
        }

        if (!(request.amount() <= 0) && request.availability() == ProductAvailability.OUT_OF_STOCK) {
            throw new BadRequestException("Amount higher than 0 can't be out of stock");
        }
    }
}
