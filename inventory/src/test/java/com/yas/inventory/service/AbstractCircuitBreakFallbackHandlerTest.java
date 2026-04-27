package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private final TestFallbackHandler fallbackHandler = new TestFallbackHandler();

    @Test
    void handleBodilessFallback_shouldRethrowThrowable() {
        RuntimeException throwable = new RuntimeException("fallback-error");

        Throwable thrown = assertThrows(Throwable.class, () -> fallbackHandler.callBodiless(throwable));

        assertSame(throwable, thrown);
    }

    @Test
    void handleTypedFallback_shouldRethrowThrowable() {
        RuntimeException throwable = new RuntimeException("fallback-error");

        Throwable thrown = assertThrows(Throwable.class, () -> fallbackHandler.callTyped(throwable));

        assertSame(throwable, thrown);
    }

    @Test
    void handleTypedFallback_whenThrowableIsNull_shouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> fallbackHandler.callTyped(null));
    }

    private static final class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        private void callBodiless(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }

        private <T> T callTyped(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }
    }
}
