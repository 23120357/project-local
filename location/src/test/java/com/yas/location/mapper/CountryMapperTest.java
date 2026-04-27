package com.yas.location.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.location.model.Country;
import com.yas.location.viewmodel.country.CountryPostVm;
import com.yas.location.viewmodel.country.CountryVm;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class CountryMapperTest {

    private final CountryMapper countryMapper = Mappers.getMapper(CountryMapper.class);

    @Test
    void toCountryFromCountryPostViewModel_whenDtoIsNull_thenReturnNull() {
        Country country = countryMapper.toCountryFromCountryPostViewModel(null);

        assertNull(country);
    }

    @Test
    void toCountryFromCountryPostViewModel_whenValidDto_thenMapAllFieldsAndIgnoreId() {
        CountryPostVm countryPostVm = CountryPostVm.builder()
            .id("country-id")
            .name("Vietnam")
            .code2("VN")
            .code3("VNM")
            .isBillingEnabled(true)
            .isShippingEnabled(true)
            .isCityEnabled(true)
            .isZipCodeEnabled(false)
            .isDistrictEnabled(true)
            .build();

        Country country = countryMapper.toCountryFromCountryPostViewModel(countryPostVm);

        assertNull(country.getId());
        assertEquals("Vietnam", country.getName());
        assertEquals("VN", country.getCode2());
        assertEquals("VNM", country.getCode3());
        assertTrue(country.getIsBillingEnabled());
        assertTrue(country.getIsShippingEnabled());
        assertTrue(country.getIsCityEnabled());
        assertFalse(country.getIsZipCodeEnabled());
        assertTrue(country.getIsDistrictEnabled());
    }

    @Test
    void toCountryFromCountryPostViewModel_whenUpdateWithNullFields_thenKeepExistingValues() {
        Country existingCountry = Country.builder()
            .id(10L)
            .name("Old Name")
            .code2("OL")
            .code3("OLD")
            .isBillingEnabled(true)
            .isShippingEnabled(true)
            .isCityEnabled(true)
            .isZipCodeEnabled(true)
            .isDistrictEnabled(true)
            .build();

        CountryPostVm updateVm = CountryPostVm.builder()
            .id("ignored")
            .name("New Name")
            .code2(null)
            .code3(null)
            .isBillingEnabled(null)
            .isShippingEnabled(false)
            .isCityEnabled(null)
            .isZipCodeEnabled(false)
            .isDistrictEnabled(null)
            .build();

        countryMapper.toCountryFromCountryPostViewModel(existingCountry, updateVm);

        assertEquals(10L, existingCountry.getId());
        assertEquals("New Name", existingCountry.getName());
        assertEquals("OL", existingCountry.getCode2());
        assertEquals("OLD", existingCountry.getCode3());
        assertTrue(existingCountry.getIsBillingEnabled());
        assertFalse(existingCountry.getIsShippingEnabled());
        assertTrue(existingCountry.getIsCityEnabled());
        assertFalse(existingCountry.getIsZipCodeEnabled());
        assertTrue(existingCountry.getIsDistrictEnabled());
    }

    @Test
    void toCountryFromCountryPostViewModel_whenUpdateDtoIsNull_thenDoNothing() {
        Country existingCountry = Country.builder()
            .id(10L)
            .name("Old Name")
            .code2("OL")
            .code3("OLD")
            .isBillingEnabled(true)
            .isShippingEnabled(true)
            .isCityEnabled(true)
            .isZipCodeEnabled(true)
            .isDistrictEnabled(true)
            .build();

        countryMapper.toCountryFromCountryPostViewModel(existingCountry, null);

        assertEquals(10L, existingCountry.getId());
        assertEquals("Old Name", existingCountry.getName());
        assertEquals("OL", existingCountry.getCode2());
        assertEquals("OLD", existingCountry.getCode3());
        assertTrue(existingCountry.getIsBillingEnabled());
        assertTrue(existingCountry.getIsShippingEnabled());
        assertTrue(existingCountry.getIsCityEnabled());
        assertTrue(existingCountry.getIsZipCodeEnabled());
        assertTrue(existingCountry.getIsDistrictEnabled());
    }

    @Test
    void toCountryViewModelFromCountry_whenCountryIsNull_thenReturnNull() {
        CountryVm countryVm = countryMapper.toCountryViewModelFromCountry(null);

        assertNull(countryVm);
    }

    @Test
    void toCountryViewModelFromCountry_whenValidCountry_thenMapAllFields() {
        Country country = Country.builder()
            .id(20L)
            .name("Japan")
            .code2("JP")
            .code3("JPN")
            .isBillingEnabled(false)
            .isShippingEnabled(true)
            .isCityEnabled(true)
            .isZipCodeEnabled(false)
            .isDistrictEnabled(false)
            .build();

        CountryVm countryVm = countryMapper.toCountryViewModelFromCountry(country);

        assertEquals(20L, countryVm.id());
        assertEquals("Japan", countryVm.name());
        assertEquals("JP", countryVm.code2());
        assertEquals("JPN", countryVm.code3());
        assertFalse(countryVm.isBillingEnabled());
        assertTrue(countryVm.isShippingEnabled());
        assertTrue(countryVm.isCityEnabled());
        assertFalse(countryVm.isZipCodeEnabled());
        assertFalse(countryVm.isDistrictEnabled());
    }
}