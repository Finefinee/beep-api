package com.b.beep.global.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders

@Configuration
class SwaggerConfig {
    @Bean
    fun api(): OpenAPI =
        OpenAPI().info(Info().title("Beep").description("Beep API Documentation").version("v0.0.?"))
            .addSecurityItem(SecurityRequirement().addList("Authorization"))
            .components(
                Components()
                    .addSecuritySchemes(
                        "Authorization", SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("Bearer")
                            .bearerFormat("Authorization")
                            .`in`(SecurityScheme.In.HEADER)
                            .name(HttpHeaders.AUTHORIZATION)
                    )
            )
}