package com.yas.location.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AddressResponseMapperTest {

    @Test
    void projectionContract_shouldExposeValuesFromImplementation() {
        AddressResponseMapper addressResponseMapper = new AddressResponseMapper() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public String getContactName() {
                return "Contact Name";
            }

            @Override
            public String getPhone() {
                return "0900000000";
            }

            @Override
            public String getAddressLine1() {
                return "Line 1";
            }
        };

        assertEquals(1L, addressResponseMapper.getId());
        assertEquals("Contact Name", addressResponseMapper.getContactName());
        assertEquals("0900000000", addressResponseMapper.getPhone());
        assertEquals("Line 1", addressResponseMapper.getAddressLine1());
    }
}