package com.yas.tax.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootTest(classes = SecurityConfigSpringTest.TestApplication.class)
class SecurityConfigSpringTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(SecurityConfig.class)
    static class TestApplication {
    }

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void filterChainBeanShouldBeCreated() {
        assertThat(securityFilterChain).isNotNull();
    }
}
