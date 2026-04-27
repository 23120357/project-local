package com.yas.inventory.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

class SecurityConfigTest {

    @Test
    void filterChain_shouldBuildSuccessfully() throws Exception {
        SecurityConfig config = new SecurityConfig();
        HttpSecurity http = mock(HttpSecurity.class);
        SecurityFilterChain chain = mock(SecurityFilterChain.class);

        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.oauth2ResourceServer(any())).thenReturn(http);
        when(http.build()).thenReturn(chain);

        SecurityFilterChain result = config.filterChain(http);

        assertNotNull(result);
    }

    @Test
    void jwtAuthenticationConverterForKeycloak_shouldMapRealmRolesToAuthorities() {
        SecurityConfig config = new SecurityConfig();
        JwtAuthenticationConverter converter = config.jwtAuthenticationConverterForKeycloak();

        Jwt jwt = Jwt.withTokenValue("test-token")
            .header("alg", "none")
            .claim("realm_access", Map.of("roles", List.of("ADMIN", "MANAGER")))
            .build();

        AbstractAuthenticationToken authentication = converter.convert(jwt);
        List<String> authorities = authentication.getAuthorities().stream()
            .map(grantedAuthority -> grantedAuthority.getAuthority())
            .collect(Collectors.toList());

        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertTrue(authorities.contains("ROLE_MANAGER"));
    }
}
