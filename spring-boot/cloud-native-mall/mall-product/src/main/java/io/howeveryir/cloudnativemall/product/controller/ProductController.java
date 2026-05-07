package io.howeveryir.cloudnativemall.product.controller;

import io.howeveryir.cloudnativemall.common.core.ApiResponse;
import io.howeveryir.cloudnativemall.common.core.ErrorCode;
import io.howeveryir.cloudnativemall.common.core.PageResult;
import io.howeveryir.cloudnativemall.product.model.ProductItem;
import io.howeveryir.cloudnativemall.product.service.ProductService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<PageResult<ProductItem>> list(@RequestParam(defaultValue = "1") @Min(value = 1, message = "pageNo must >= 1") int pageNo,
                                                     @RequestParam(defaultValue = "10") @Min(value = 1, message = "pageSize must >= 1")
                                                     @Max(value = 100, message = "pageSize must <= 100") int pageSize) {
        List<ProductItem> products = productService.listProducts(pageNo, pageSize);
        return ApiResponse.ok(new PageResult<>(products, productService.totalCount(), pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductItem> detail(@PathVariable @Positive(message = "id must be positive") Long id) {
        ProductItem product = productService.getById(id);
        if (product == null) {
            return ApiResponse.fail(ErrorCode.PRODUCT_NOT_FOUND, "product not found");
        }
        return ApiResponse.ok(product);
    }
}
