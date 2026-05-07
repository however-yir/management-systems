package io.howeveryir.cloudnativemall.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.howeveryir.cloudnativemall.product.model.ProductItem;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ProductService {

    private static final String DETAIL_CACHE_KEY = "mall:product:detail:";
    private static final String NULL_CACHE_KEY = "mall:product:null:";
    private static final String LOCK_KEY = "mall:product:lock:";

    private static final Duration PRODUCT_CACHE_BASE_TTL = Duration.ofMinutes(5);
    private static final Duration NULL_CACHE_BASE_TTL = Duration.ofSeconds(30);
    private static final Duration LOCK_TTL = Duration.ofSeconds(5);

    private static final List<ProductItem> MOCK_PRODUCTS = List.of(
            new ProductItem(1001L, "Mechanical Keyboard", "electronics", BigDecimal.valueOf(399.00), 120),
            new ProductItem(1002L, "Wireless Mouse", "electronics", BigDecimal.valueOf(129.00), 200),
            new ProductItem(1003L, "USB-C Dock", "accessories", BigDecimal.valueOf(259.00), 80),
            new ProductItem(1004L, "4K Monitor", "electronics", BigDecimal.valueOf(1799.00), 32)
    );

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ProductService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public List<ProductItem> listProducts() {
        return MOCK_PRODUCTS;
    }

    public List<ProductItem> listProducts(int pageNo, int pageSize) {
        int from = Math.max((pageNo - 1) * pageSize, 0);
        if (from >= MOCK_PRODUCTS.size()) {
            return List.of();
        }
        int to = Math.min(from + pageSize, MOCK_PRODUCTS.size());
        return MOCK_PRODUCTS.subList(from, to);
    }

    public int totalCount() {
        return MOCK_PRODUCTS.size();
    }

    public ProductItem getById(Long id) {
        ProductItem cached = getFromCache(id);
        if (cached != null) {
            return cached;
        }
        if (existsNullCache(id)) {
            return null;
        }

        String lockKey = LOCK_KEY + id;
        String lockValue = String.valueOf(System.nanoTime());
        boolean locked = tryAcquireLock(lockKey, lockValue);

        try {
            if (!locked) {
                ProductItem retryCached = getFromCache(id);
                if (retryCached != null) {
                    return retryCached;
                }
                if (existsNullCache(id)) {
                    return null;
                }
            }

            ProductItem product = loadFromSource(id);
            if (product == null) {
                cacheNull(id);
                return null;
            }

            cacheProduct(id, product);
            return product;
        } finally {
            if (locked) {
                releaseLock(lockKey, lockValue);
            }
        }
    }

    private ProductItem loadFromSource(Long id) {
        return MOCK_PRODUCTS.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private ProductItem getFromCache(Long id) {
        String cacheKey = DETAIL_CACHE_KEY + id;
        try {
            String cacheValue = redisTemplate.opsForValue().get(cacheKey);
            if (cacheValue == null || cacheValue.isBlank()) {
                return null;
            }
            return objectMapper.readValue(cacheValue, ProductItem.class);
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean existsNullCache(Long id) {
        String nullKey = NULL_CACHE_KEY + id;
        try {
            Boolean exists = redisTemplate.hasKey(nullKey);
            return Boolean.TRUE.equals(exists);
        } catch (Exception ignored) {
            return false;
        }
    }

    private void cacheProduct(Long id, ProductItem product) {
        String cacheKey = DETAIL_CACHE_KEY + id;
        try {
            String cacheValue = objectMapper.writeValueAsString(product);
            redisTemplate.opsForValue().set(cacheKey, cacheValue, withJitter(PRODUCT_CACHE_BASE_TTL, 30));
        } catch (JsonProcessingException ignored) {
            // Ignore serialization failure and fallback to source data on next query.
        } catch (Exception ignored) {
            // Ignore redis unavailable exceptions, keep service degraded but available.
        }
    }

    private void cacheNull(Long id) {
        String nullKey = NULL_CACHE_KEY + id;
        try {
            redisTemplate.opsForValue().set(nullKey, "1", withJitter(NULL_CACHE_BASE_TTL, 20));
        } catch (Exception ignored) {
            // Ignore redis unavailable exceptions, keep service degraded but available.
        }
    }

    private boolean tryAcquireLock(String lockKey, String value) {
        try {
            Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, value, LOCK_TTL);
            return Boolean.TRUE.equals(acquired);
        } catch (Exception ignored) {
            return false;
        }
    }

    private void releaseLock(String lockKey, String value) {
        try {
            String currentValue = redisTemplate.opsForValue().get(lockKey);
            if (value.equals(currentValue)) {
                redisTemplate.delete(lockKey);
            }
        } catch (Exception ignored) {
            // Ignore lock release failure in degraded mode.
        }
    }

    private Duration withJitter(Duration base, int maxJitterSeconds) {
        int jitter = ThreadLocalRandom.current().nextInt(maxJitterSeconds + 1);
        return base.plusSeconds(jitter);
    }
}
