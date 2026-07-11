package com.cooksmart.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI cookSmartOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CookSmart AI API")
                        .description(
                                "REST API for generating personalized daily cooking plans, grocery lists, "
                                        + "ingredient substitutions, and budget feasibility analysis.")
                        .version("1.0.0")
                        .contact(new Contact().name("CookSmart AI").email("hello@cooksmart.ai"))
                        .license(new License().name("MIT")))
                .servers(List.of(new Server().url("http://localhost:" + serverPort).description("Local")));
    }
}
