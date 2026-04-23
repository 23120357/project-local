package com.yas.location.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_whenErrorCodeExists_thenReturnFormattedMessage() {
        String message = MessagesUtils.getMessage(Constants.ErrorCode.COUNTRY_NOT_FOUND, 10);

        assertEquals("The country 10 is not found", message);
    }

    @Test
    void getMessage_whenErrorCodeMissing_thenReturnErrorCodeItself() {
        String message = MessagesUtils.getMessage("UNKNOWN_ERROR_CODE", 10);

        assertEquals("UNKNOWN_ERROR_CODE", message);
    }
}
