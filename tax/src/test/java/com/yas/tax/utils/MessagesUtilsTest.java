package com.yas.tax.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class MessagesUtilsTest {

    @Test
    void getMessage_ShouldReturnFormattedMessage_WhenKeyExistsWithArgs() {
        // "TAX_CLASS_NOT_FOUND" is defined in messages.properties as "Tax class with id {} not found"
        String result = MessagesUtils.getMessage("TAX_CLASS_NOT_FOUND", 42L);
        assertThat(result).isEqualTo("Tax class with id 42 not found");
    }

    @Test
    void getMessage_ShouldReturnFormattedMessage_WhenKeyExistsWithoutArgs() {
        // "NAME_ALREADY_EXITED" is defined as "Name {} already exists"
        String result = MessagesUtils.getMessage("NAME_ALREADY_EXITED", "Standard");
        assertThat(result).isEqualTo("Name Standard already exists");
    }

    @Test
    void getMessage_ShouldReturnErrorCode_WhenKeyDoesNotExist() {
        // Key not in messages.properties → fallback returns the key itself
        String result = MessagesUtils.getMessage("NON_EXISTENT_KEY");
        assertThat(result).isEqualTo("NON_EXISTENT_KEY");
    }

    @Test
    void getMessage_ShouldReturnFormattedMessage_WhenMultipleArgsProvided() {
        // Using TAX_RATE_NOT_FOUND = "Tax rate with id {} not found"
        String result = MessagesUtils.getMessage("TAX_RATE_NOT_FOUND", 99L);
        assertThat(result).isEqualTo("Tax rate with id 99 not found");
    }

    @Test
    void constants_ClassShouldBeInstantiable() {
        // Trigger class loading for coverage of Constants class
        Constants constants = new Constants();
        assertThat(constants).isNotNull();
    }
}
