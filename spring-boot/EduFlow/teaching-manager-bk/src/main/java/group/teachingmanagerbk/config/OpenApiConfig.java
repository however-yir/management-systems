package group.teachingmanagerbk.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eduFlowOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("EduFlow Teaching Manager API")
                        .description("EduFlow 教务管理系统后端接口文档")
                        .version("v1.0.0")
                        .contact(new Contact().name("EduFlow Team"))
                        .license(new License().name("MIT")));
    }
}

