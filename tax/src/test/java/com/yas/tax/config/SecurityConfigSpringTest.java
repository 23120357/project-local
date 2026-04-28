package com.yas.tax.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootTest(classes = SecurityConfig.class)
class SecurityConfigSpringTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void filterChainBeanShouldBeCreated() {
        assertThat(securityFilterChain).isNotNull();
    }
}
