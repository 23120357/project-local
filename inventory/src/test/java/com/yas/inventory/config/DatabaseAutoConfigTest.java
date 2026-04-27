package com.yas.inventory.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class DatabaseAutoConfigTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void auditorAware_whenAuthenticationIsNull_shouldReturnEmptyString() {
        DatabaseAutoConfig config = new DatabaseAutoConfig();
        SecurityContextHolder.clearContext();

        AuditorAware<String> auditorAware = config.auditorAware();

        assertEquals("", auditorAware.getCurrentAuditor().orElse("unexpected"));
    }

    @Test
    void auditorAware_whenAuthenticationExists_shouldReturnPrincipalName() {
        DatabaseAutoConfig config = new DatabaseAutoConfig();
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("admin-user", "n/a"));

        AuditorAware<String> auditorAware = config.auditorAware();

        assertEquals("admin-user", auditorAware.getCurrentAuditor().orElse("unexpected"));
    }
}
