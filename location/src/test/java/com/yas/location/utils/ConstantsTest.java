package com.yas.location.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ConstantsTest {

    @Test
    void constructors_shouldCreateAllConstantsTypes() {
        Constants constants = new Constants();
        Constants.ErrorCode errorCode = constants.new ErrorCode();
        Constants.PageableConstant pageableConstant = constants.new PageableConstant();
        Constants.ApiConstant apiConstant = constants.new ApiConstant();

        assertNotNull(constants);
        assertNotNull(errorCode);
        assertNotNull(pageableConstant);
        assertNotNull(apiConstant);
    }

    @Test
    void constants_shouldKeepExpectedValues() {
        assertEquals("COUNTRY_NOT_FOUND", Constants.ErrorCode.COUNTRY_NOT_FOUND);
        assertEquals("NAME_ALREADY_EXITED", Constants.ErrorCode.NAME_ALREADY_EXITED);
        assertEquals("STATE_OR_PROVINCE_NOT_FOUND", Constants.ErrorCode.STATE_OR_PROVINCE_NOT_FOUND);
        assertEquals("ADDRESS_NOT_FOUND", Constants.ErrorCode.ADDRESS_NOT_FOUND);
        assertEquals("CODE_ALREADY_EXISTED", Constants.ErrorCode.CODE_ALREADY_EXISTED);

        assertEquals("10", Constants.PageableConstant.DEFAULT_PAGE_SIZE);
        assertEquals("0", Constants.PageableConstant.DEFAULT_PAGE_NUMBER);

        assertEquals("/backoffice/state-or-provinces", Constants.ApiConstant.STATE_OR_PROVINCES_URL);
        assertEquals("/storefront/state-or-provinces", Constants.ApiConstant.STATE_OR_PROVINCES_STOREFRONT_URL);
        assertEquals("/backoffice/countries", Constants.ApiConstant.COUNTRIES_URL);
        assertEquals("/storefront/countries", Constants.ApiConstant.COUNTRIES_STOREFRONT_URL);

        assertEquals("200", Constants.ApiConstant.CODE_200);
        assertEquals("Ok", Constants.ApiConstant.OK);
        assertEquals("404", Constants.ApiConstant.CODE_404);
        assertEquals("Not found", Constants.ApiConstant.NOT_FOUND);
        assertEquals("201", Constants.ApiConstant.CODE_201);
        assertEquals("Created", Constants.ApiConstant.CREATED);
        assertEquals("400", Constants.ApiConstant.CODE_400);
        assertEquals("Bad request", Constants.ApiConstant.BAD_REQUEST);
        assertEquals("204", Constants.ApiConstant.CODE_204);
        assertEquals("No content", Constants.ApiConstant.NO_CONTENT);
    }
}