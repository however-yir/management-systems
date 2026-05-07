package io.howeveryir.cloudnativemall.order;

import io.howeveryir.cloudnativemall.order.persistence.OrderEntity;
import io.howeveryir.cloudnativemall.order.persistence.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false",
        "management.tracing.enabled=false",
        "seata.enabled=false"
})
class OrderInfrastructureIntegrationTests {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.37")
            .withDatabaseName("mall_order")
            .withUsername("test")
            .withPassword("test");

    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7.2"))
            .withExposedPorts(6379);

    @Container
    static final ElasticsearchContainer ELASTICSEARCH = new ElasticsearchContainer(
            DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.15.0"))
            .withEnv("xpack.security.enabled", "false")
            .withEnv("discovery.type", "single-node");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));

        registry.add("spring.elasticsearch.uris", () -> "http://" + ELASTICSEARCH.getHost() + ":" + ELASTICSEARCH.getMappedPort(9200));
    }

    @Test
    void shouldStartContainersAndPersistOrder() {
        assertTrue(MYSQL.isRunning());
        assertTrue(REDIS.isRunning());
        assertTrue(ELASTICSEARCH.isRunning());

        OrderEntity entity = new OrderEntity();
        entity.setOrderNo("OD" + System.currentTimeMillis());
        entity.setUserId(1L);
        entity.setProductId(1001L);
        entity.setQuantity(1);
        entity.setStatus("CREATED");

        OrderEntity saved = orderRepository.save(entity);
        assertNotNull(saved.getId());
        assertTrue(orderRepository.findByOrderNo(saved.getOrderNo()).isPresent());

        redisTemplate.opsForValue().set("mall:integration:test", "ok");
        assertEquals("ok", redisTemplate.opsForValue().get("mall:integration:test"));
    }
}
