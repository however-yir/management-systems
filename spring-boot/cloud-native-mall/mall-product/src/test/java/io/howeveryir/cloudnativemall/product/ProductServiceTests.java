package io.howeveryir.cloudnativemall.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.howeveryir.cloudnativemall.product.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;

class ProductServiceTests {

    private final ProductService productService =
            new ProductService(Mockito.mock(StringRedisTemplate.class), new ObjectMapper());

    @Test
    void shouldPaginateProducts() {
        Assertions.assertEquals(2, productService.listProducts(1, 2).size());
        Assertions.assertEquals(2, productService.listProducts(2, 2).size());
        Assertions.assertTrue(productService.listProducts(3, 2).isEmpty());
        Assertions.assertEquals(4, productService.totalCount());
    }
}
