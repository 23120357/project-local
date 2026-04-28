package com.yas.tax.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ServiceUrlConfigTest {

    @Test
    void serviceUrlConfig_shouldExposeLocation() {
        ServiceUrlConfig config = new ServiceUrlConfig("http://localhost:8080");

        assertThat(config.location()).isEqualTo("http://localhost:8080");
    }
}
