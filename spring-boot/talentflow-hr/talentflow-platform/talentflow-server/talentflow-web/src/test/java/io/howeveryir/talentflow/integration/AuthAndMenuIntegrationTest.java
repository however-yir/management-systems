package io.howeveryir.talentflow.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class AuthAndMenuIntegrationTest {

    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            .withDatabaseName("talentflow_hr")
            .withUsername("root")
            .withPassword("talentflow");

    @Container
    private static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", AuthAndMenuIntegrationTest::jdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.druid.web-stat-filter.enabled", () -> "false");
        registry.add("spring.datasource.druid.stat-view-servlet.enabled", () -> "false");
        registry.add("spring.redis.host", REDIS::getHost);
        registry.add("spring.redis.port", () -> REDIS.getMappedPort(6379));
        registry.add("spring.task.scheduling.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("update hr set password = ? where username = ?", passwordEncoder.encode("123"), "admin");
        stringRedisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    void systemMenuShouldReturnUnauthorizedWhenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/system/config/menu"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.msg").exists());
    }

    @Test
    void doLoginShouldSucceedWithMysqlBackedUser() throws Exception {
        mockMvc.perform(loginRequest("admin", "123", "abcd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("登录成功!"))
                .andExpect(jsonPath("$.obj.username").value("admin"));
    }

    @Test
    void doLoginShouldFailWhenVerifyCodeIncorrect() throws Exception {
        mockMvc.perform(loginRequest("admin", "123", "wrong-code"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.msg").value("验证码不正确"));
    }

    @Test
    void doLoginShouldFailWhenPasswordIncorrect() throws Exception {
        mockMvc.perform(loginRequest("admin", "wrong-pass", "abcd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.msg").value("用户名或者密码输入错误，请重新输入!"));
    }

    @Test
    void systemMenuEndpointShouldReturnMenuAfterLoginAndPopulateRedisCache() throws Exception {
        MockHttpSession verifyCodeSession = verifyCodeSession();
        MvcResult loginResult = mockMvc.perform(loginRequest("admin", "123", "abcd", verifyCodeSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andReturn();

        MockHttpSession authenticatedSession = (MockHttpSession) loginResult.getRequest().getSession(false);
        if (authenticatedSession == null) {
            authenticatedSession = verifyCodeSession;
        }
        SecurityContext securityContext = (SecurityContext) authenticatedSession.getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
        );
        assertThat(securityContext).as("security context after successful login").isNotNull();
        assertThat(securityContext.getAuthentication()).as("authenticated principal after successful login").isNotNull();

        mockMvc.perform(get("/system/config/menu").session(authenticatedSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].children").isArray());

        mockMvc.perform(get("/system/config/menu").session(authenticatedSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists());

        assertThat(jdbcTemplate.queryForObject("select count(*) from hr", Integer.class)).isGreaterThan(0);
        assertThat(stringRedisTemplate.getConnectionFactory().getConnection().ping()).isEqualTo("PONG");
        Set<String> cacheKeys = stringRedisTemplate.keys("menus_cache*");
        assertThat(cacheKeys).isNotNull();
        assertThat(cacheKeys).isNotEmpty();
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder loginRequest(
            String username,
            String password,
            String code
    ) {
        return loginRequest(username, password, code, verifyCodeSession());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder loginRequest(
            String username,
            String password,
            String code,
            MockHttpSession session
    ) {
        return post("/doLogin")
                .session(session)
                .contentType(APPLICATION_JSON)
                .content(String.format("{\"username\":\"%s\",\"password\":\"%s\",\"code\":\"%s\"}", username, password, code));
    }

    private MockHttpSession verifyCodeSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("verify_code", "ABCD");
        return session;
    }

    private static String jdbcUrl() {
        String url = MYSQL.getJdbcUrl();
        if (url.contains("?")) {
            return url + "&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        }
        return url + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
    }
}
