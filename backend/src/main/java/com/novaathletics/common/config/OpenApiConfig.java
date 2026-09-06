package com.novaathletics.common.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;
@Configuration
public class OpenApiConfig {
  @Bean OpenAPI openAPI(){ return new OpenAPI().info(new Info().title("NOVA Athletics API").version("1.0.0").description("Modular monolith API")); }
}
