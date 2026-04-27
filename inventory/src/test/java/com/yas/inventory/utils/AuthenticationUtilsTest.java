package com.yas.inventory.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.AccessDeniedException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthenticationUtilsTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void extractUserId_whenAnonymousAuthentication_shouldThrow() {
        SecurityContext context = mock(SecurityContext.class);
        AnonymousAuthenticationToken anonymousAuthenticationToken = new AnonymousAuthenticationToken(
            "key",
            "anonymousUser",
            List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        );
        when(context.getAuthentication()).thenReturn(anonymousAuthenticationToken);
        SecurityContextHolder.setContext(context);

        assertThrows(AccessDeniedException.class, AuthenticationUtils::extractUserId);
    }

    @Test
    void extractUserId_whenJwtAuthentication_shouldReturnSubject() {
        Jwt jwt = Jwt.withTokenValue("jwt-token")
            .header("alg", "none")
            .claim("sub", "user-1")
            .build();
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(jwtAuthenticationToken);
        SecurityContextHolder.setContext(context);

        assertEquals("user-1", AuthenticationUtils.extractUserId());
    }

    @Test
    void extractJwt_shouldReturnTokenValue() {
        Jwt jwt = Jwt.withTokenValue("token-xyz")
            .header("alg", "none")
            .claim("sub", "user-1")
            .build();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        assertEquals("token-xyz", AuthenticationUtils.extractJwt());
    }
}