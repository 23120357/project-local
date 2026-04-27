package com.yas.tax.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

@SpringBootTest(classes = {SecurityConfig.class, RestClientConfig.class})
@ExtendWith(SpringExtension.class)
public class SecurityConfigTest {

    @Autowired
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @Autowired
    private RestClient restClient;

    @Test
    void jwtAuthenticationConverter_ShouldBeNotNull() {
        assertThat(jwtAuthenticationConverter).isNotNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    void jwtAuthenticationConverter_ShouldMapRolesToGrantedAuthorities() {
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
    void restClient_ShouldBeNotNull() {
        assertThat(restClient).isNotNull();
    }
}
