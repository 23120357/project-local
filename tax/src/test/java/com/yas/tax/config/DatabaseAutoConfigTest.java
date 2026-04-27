package com.yas.tax.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = DatabaseAutoConfig.class)
@ExtendWith(SpringExtension.class)
public class DatabaseAutoConfigTest {

    @Autowired
    private AuditorAware<String> auditorAware;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void auditorAware_ShouldReturnEmptyString_WhenAuthenticationIsNull() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        Optional<String> result = auditorAware.getCurrentAuditor();

        assertThat(result).isPresent();
        assertThat(result.get()).isEmpty();
    }

    @Test
    void auditorAware_ShouldReturnUsername_WhenAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        Optional<String> result = auditorAware.getCurrentAuditor();

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("testuser");
    }
}
