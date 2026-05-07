package io.howeveryir.cloudnativemall.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false",
        "management.tracing.enabled=false",
        "MALL_BOOTSTRAP_ADMIN_PASSWORD=Mall@123",
        "MALL_BOOTSTRAP_ALICE_PASSWORD=Alice@123",
        "MALL_BOOTSTRAP_BOB_PASSWORD=Bob@123"
})
@AutoConfigureMockMvc
class UserOpenApiContractTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldExposeUserApiInOpenApiDocument() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/users/login']").exists())
                .andExpect(jsonPath("$.paths['/api/users/{id}']").exists());
    }

    @Test
    void loginContractShouldReturnTokenFields() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"mall-admin\",\"password\":\"Mall@123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").isNotEmpty())
                .andExpect(jsonPath("$.data.roles").isNotEmpty());
    }
}
