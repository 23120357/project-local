package com.yas.tax.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.junit.jupiter.api.Test;

class SwaggerConfigTest {

    @Test
    void swaggerConfig_shouldHaveOpenApiAnnotations() {
        OpenAPIDefinition openApiDefinition = SwaggerConfig.class.getAnnotation(OpenAPIDefinition.class);
        SecurityScheme securityScheme = SwaggerConfig.class.getAnnotation(SecurityScheme.class);

        assertThat(openApiDefinition).isNotNull();
        assertThat(securityScheme).isNotNull();
    }
}
