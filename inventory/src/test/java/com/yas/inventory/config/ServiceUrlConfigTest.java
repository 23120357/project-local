package com.yas.inventory.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ServiceUrlConfigTest {

    @Test
    void constructor_shouldSetFields() {
        ServiceUrlConfig config = new ServiceUrlConfig("http://product", "http://location");

        assertEquals("http://product", config.product());
        assertEquals("http://location", config.location());
    }
}
