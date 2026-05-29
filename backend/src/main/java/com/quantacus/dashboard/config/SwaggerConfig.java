package com.quantacus.dashboard.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Intelligence Dashboard API")
                        .description("""
                                Backend API for the Quantacus Product Intelligence Dashboard.

                                Allows e-commerce sellers to:
                                - Upload a product video or CSV
                                - Extract and validate product listing quality
                                - Generate enhanced product titles
                                - Compare prices against simulated competitors
                                - View and resolve listing alerts
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Quantacus Engineering")
                                .email("engineering@quantacus.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development"),
                        new Server()
                                .url("https://api.your-deployment.com")
                                .description("Production")));
    }
}
