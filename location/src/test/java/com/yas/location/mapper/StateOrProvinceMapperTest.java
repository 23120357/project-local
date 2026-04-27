package com.yas.location.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.location.model.Country;
import com.yas.location.model.StateOrProvince;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class StateOrProvinceMapperTest {

    private final StateOrProvinceMapper stateOrProvinceMapper = Mappers.getMapper(StateOrProvinceMapper.class);

    @Test
    void toStateOrProvinceViewModelFromStateOrProvince_whenStateOrProvinceIsNull_thenReturnNull() {
        StateOrProvinceVm stateOrProvinceVm = stateOrProvinceMapper.toStateOrProvinceViewModelFromStateOrProvince(null);

        assertNull(stateOrProvinceVm);
    }

    @Test
    void toStateOrProvinceViewModelFromStateOrProvince_whenCountryIsNull_thenCountryIdIsNull() {
        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(2L)
            .name("Ho Chi Minh")
            .code("HCM")
            .type("CITY")
            .country(null)
            .build();

        StateOrProvinceVm stateOrProvinceVm = stateOrProvinceMapper
            .toStateOrProvinceViewModelFromStateOrProvince(stateOrProvince);

        assertNotNull(stateOrProvinceVm);
        assertEquals(2L, stateOrProvinceVm.id());
        assertEquals("Ho Chi Minh", stateOrProvinceVm.name());
        assertEquals("HCM", stateOrProvinceVm.code());
        assertEquals("CITY", stateOrProvinceVm.type());
        assertNull(stateOrProvinceVm.countryId());
    }

    @Test
    void toStateOrProvinceViewModelFromStateOrProvince_whenValidStateOrProvince_thenMapAllFields() {
        Country country = Country.builder()
            .id(1L)
            .name("Vietnam")
            .code2("VN")
            .build();

        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(3L)
            .name("Ha Noi")
            .code("HN")
            .type("CITY")
            .country(country)
            .build();

        StateOrProvinceVm stateOrProvinceVm = stateOrProvinceMapper
            .toStateOrProvinceViewModelFromStateOrProvince(stateOrProvince);

        assertNotNull(stateOrProvinceVm);
        assertEquals(3L, stateOrProvinceVm.id());
        assertEquals("Ha Noi", stateOrProvinceVm.name());
        assertEquals("HN", stateOrProvinceVm.code());
        assertEquals("CITY", stateOrProvinceVm.type());
        assertEquals(1L, stateOrProvinceVm.countryId());
    }
}