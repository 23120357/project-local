package com.yas.tax.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

public class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void jwtAuthenticationConverterForKeycloak_ShouldMapRolesToGrantedAuthorities() {
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();
        assertThat(converter).isNotNull();

        // Verify the converter maps JWT roles to ROLE_ prefixed authorities
        Jwt jwt = mock(Jwt.class);
        Map<String, Collection<String>> realmAccess = Map.of("roles", List.of("ADMIN", "USER"));
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);
        // Additional token claims required by JwtAuthenticationConverter
        when(jwt.getSubject()).thenReturn("test-subject");
        when(jwt.getClaims()).thenReturn(Map.of("realm_access", realmAccess, "sub", "test-subject"));
        when(jwt.getTokenValue()).thenReturn("mock-token");

        // Extract the grantedAuthoritiesConverter from the JwtAuthenticationConverter
        // by calling it via the actual converter's internal converter field
        Collection<GrantedAuthority> authorities = converter
                .getJwtGrantedAuthoritiesConverter().convert(jwt);

        assertThat(authorities).isNotNull();
        assertThat(authorities).hasSize(2);
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void restClientConfig_ShouldReturnNonNullBean() {
        RestClientConfig restClientConfig = new RestClientConfig();
        assertThat(restClientConfig.restClient()).isNotNull();
    }
}
