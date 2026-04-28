package com.yas.tax.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class DatabaseAutoConfigTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void auditorAware_shouldReturnEmptyStringWhenNoAuthentication() {
        DatabaseAutoConfig config = new DatabaseAutoConfig();
        AuditorAware<String> auditorAware = config.auditorAware();

        SecurityContextHolder.clearContext();

        Optional<String> current = auditorAware.getCurrentAuditor();

        assertThat(current).contains("");
    }

    @Test
    void auditorAware_shouldReturnAuthenticationNameWhenPresent() {
        DatabaseAutoConfig config = new DatabaseAutoConfig();
        AuditorAware<String> auditorAware = config.auditorAware();

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("alice");

        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        Optional<String> current = auditorAware.getCurrentAuditor();

        assertThat(current).contains("alice");
    }
}
