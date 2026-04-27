package com.yas.tax.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.util.ReflectionTestUtils;

public class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void jwtAuthenticationConverterForKeycloak_ShouldReturnNonNull() {
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();
        assertThat(converter).isNotNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    void jwtAuthenticationConverterForKeycloak_ShouldMapRolesToGrantedAuthorities() {
        JwtAuthenticationConverter jwtAuthenticationConverter =
                securityConfig.jwtAuthenticationConverterForKeycloak();

        // Use reflection to access the internal jwtGrantedAuthoritiesConverter field
        Converter<Jwt, Collection<GrantedAuthority>> grantedAuthoritiesConverter =
                (Converter<Jwt, Collection<GrantedAuthority>>)
                        ReflectionTestUtils.getField(
                                jwtAuthenticationConverter, "jwtGrantedAuthoritiesConverter");

        assertThat(grantedAuthoritiesConverter).isNotNull();

        Jwt jwt = mock(Jwt.class);
        Map<String, Collection<String>> realmAccess = Map.of("roles", List.of("ADMIN", "USER"));
        when(jwt.getClaim("realm_access")).thenReturn(realmAccess);

        Collection<GrantedAuthority> authorities = grantedAuthoritiesConverter.convert(jwt);

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
