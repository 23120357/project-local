package com.yas.location.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.yas.location.model.Address;
import com.yas.location.model.Country;
import com.yas.location.model.District;
import com.yas.location.model.StateOrProvince;
import com.yas.location.viewmodel.address.AddressDetailVm;
import com.yas.location.viewmodel.address.AddressGetVm;
import com.yas.location.viewmodel.address.AddressPostVm;
import com.yas.location.viewmodel.country.CountryVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceAndCountryGetNameVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
import org.junit.jupiter.api.Test;

class ViewModelMappingTest {

    @Test
    void fromModel_methods_shouldMapExpectedFields() {
        Country country = Country.builder().id(1L).name("Vietnam").code2("VN").build();
        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(2L)
            .name("Ho Chi Minh")
            .code("HCM")
            .type("CITY")
            .country(country)
            .build();
        District district = District.builder().id(3L).name("District 1").stateProvince(stateOrProvince).build();

        Address address = Address.builder()
            .id(4L)
            .contactName("Nguyen Van A")
            .phone("0900000000")
            .addressLine1("Line 1")
            .addressLine2("Line 2")
            .city("HCM")
            .zipCode("700000")
            .district(district)
            .stateOrProvince(stateOrProvince)
            .country(country)
            .build();

        AddressGetVm addressGetVm = AddressGetVm.fromModel(address);
        AddressDetailVm addressDetailVm = AddressDetailVm.fromModel(address);
        CountryVm countryVm = CountryVm.fromModel(country);
        StateOrProvinceVm stateOrProvinceVm = StateOrProvinceVm.fromModel(stateOrProvince);
        StateOrProvinceAndCountryGetNameVm nameVm = StateOrProvinceAndCountryGetNameVm.fromModel(stateOrProvince);

        assertEquals(4L, addressGetVm.id());
        assertEquals(3L, addressGetVm.districtId());
        assertEquals("District 1", addressDetailVm.districtName());
        assertEquals("Vietnam", countryVm.name());
        assertEquals(1L, stateOrProvinceVm.countryId());
        assertEquals("Ho Chi Minh", nameVm.stateOrProvinceName());
    }

    @Test
    void addressPostVm_fromModel_shouldMapInputFields() {
        AddressPostVm postVm = AddressPostVm.builder()
            .contactName("Tran B")
            .phone("0911222333")
            .addressLine1("A")
            .addressLine2("B")
            .city("HCM")
            .zipCode("700000")
            .districtId(3L)
            .stateOrProvinceId(2L)
            .countryId(1L)
            .build();

        Address address = AddressPostVm.fromModel(postVm);

        assertEquals("Tran B", address.getContactName());
        assertEquals("0911222333", address.getPhone());
        assertEquals("A", address.getAddressLine1());
        assertEquals("B", address.getAddressLine2());
        assertEquals("HCM", address.getCity());
        assertEquals("700000", address.getZipCode());
    }
}
