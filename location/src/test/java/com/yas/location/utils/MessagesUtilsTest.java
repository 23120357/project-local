package com.yas.location.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    private ResourceBundle originalBundle;

    @BeforeEach
    void setUp() {
        originalBundle = MessagesUtils.messageBundle;
    }

    @AfterEach
    void tearDown() {
        MessagesUtils.messageBundle = originalBundle;
    }

    @Test
    void constructor_shouldCreateInstance() {
        MessagesUtils messagesUtils = new MessagesUtils();

        assertNotNull(messagesUtils);
    }

    @Test
    void getMessage_whenErrorCodeExists_thenReturnFormattedMessage() {
        String message = MessagesUtils.getMessage(Constants.ErrorCode.COUNTRY_NOT_FOUND, 10);

        assertEquals("The country 10 is not found", message);
    }

    @Test
    void getMessage_whenUsingCustomBundle_thenFormatAllPlaceholders() {
        MessagesUtils.messageBundle = new ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[][] {
                    {"CUSTOM_ERROR", "Custom {} and {}"}
                };
            }
        };

        String message = MessagesUtils.getMessage("CUSTOM_ERROR", "A", 123);

        assertEquals("Custom A and 123", message);
    }

    @Test
    void getMessage_whenErrorCodeMissing_thenReturnErrorCodeItself() {
        String message = MessagesUtils.getMessage("UNKNOWN_ERROR_CODE", 10);

        assertEquals("UNKNOWN_ERROR_CODE", message);
    }
}
