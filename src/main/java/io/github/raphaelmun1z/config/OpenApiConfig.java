package io.github.raphaelmun1z.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Value("${spring.application.version:0.1.0}")
    private String appVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("WS2")
                .version(appVersion)
                .description("Documentação da API RESTful para o sistema WS2.")
                .termsOfService("https://github.com/RaphaelMun1z")
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://github.com/RaphaelMun1z")));
    }
}