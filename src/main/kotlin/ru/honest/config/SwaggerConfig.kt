package ru.honest.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun swaggerOpenApiConfig(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Honest API")
                    .version("1.0")
                    .description("OpenApi for Honest backend")
                    .termsOfService("https://swagger.io/terms/")
                    .license(License().name("Apache 2.0").url("https://springdoc.org"))
            )
    }
}