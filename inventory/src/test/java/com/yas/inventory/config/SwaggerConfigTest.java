package com.yas.inventory.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class SwaggerConfigTest {

    @Test
    void shouldInstantiateSwaggerConfig() {
        SwaggerConfig config = new SwaggerConfig();

        assertNotNull(config);
    }
}
