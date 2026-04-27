package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import com.yas.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class TaxServiceTest {

    @Mock
    private TaxRateRepository taxRateRepository;

    @Mock
    private TaxClassRepository taxClassRepository;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private TaxRateService taxRateService;

    private TaxRate taxRate;
    private TaxClass taxClass;
    private TaxRatePostVm taxRatePostVm;

    @BeforeEach
    void setUp() {
        taxClass = TaxClass.builder()
                .id(1L)
                .name("Standard Tax")
                .build();

        taxRate = TaxRate.builder()
                .id(1L)
                .rate(10.0)
                .zipCode("12345")
                .taxClass(taxClass)
                .stateOrProvinceId(1L)
                .countryId(1L)
                .build();

        // TaxRatePostVm(Double rate, String zipCode, Long taxClassId, Long stateOrProvinceId, Long countryId)
        taxRatePostVm = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
    }

    @Test
    void createTaxRate_ShouldReturnTaxRate_WhenSuccess() {
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);
        when(taxRateRepository.save(any(TaxRate.class))).thenReturn(taxRate);

        TaxRate result = taxRateService.createTaxRate(taxRatePostVm);

        assertThat(result.getRate()).isEqualTo(10.0);
        assertThat(result.getZipCode()).isEqualTo("12345");
        assertThat(result.getTaxClass()).isEqualTo(taxClass);
    }

    @Test
    void createTaxRate_ShouldThrowNotFoundException_WhenTaxClassNotFound() {
        when(taxClassRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxRateService.createTaxRate(taxRatePostVm));
    }

    @Test
    void updateTaxRate_ShouldSuccess_WhenFound() {
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);

        taxRateService.updateTaxRate(taxRatePostVm, 1L);

        verify(taxRateRepository).save(taxRate);
        assertThat(taxRate.getRate()).isEqualTo(10.0);
    }

    @Test
    void updateTaxRate_ShouldThrowNotFoundException_WhenTaxRateNotFound() {
        when(taxRateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxRateService.updateTaxRate(taxRatePostVm, 1L));
    }

    @Test
    void updateTaxRate_ShouldThrowNotFoundException_WhenTaxClassNotFound() {
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));
        when(taxClassRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxRateService.updateTaxRate(taxRatePostVm, 1L));
    }

    @Test
    void delete_ShouldSuccess_WhenFound() {
        when(taxRateRepository.existsById(1L)).thenReturn(true);

        taxRateService.delete(1L);

        verify(taxRateRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenNotFound() {
        when(taxRateRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxRateService.delete(1L));
    }

    @Test
    void findById_ShouldReturnTaxRateVm_WhenFound() {
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));

        TaxRateVm result = taxRateService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.rate()).isEqualTo(10.0);
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenNotFound() {
        when(taxRateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxRateService.findById(1L));
    }

    @Test
    void findAll_ShouldReturnListOfTaxRateVm() {
        when(taxRateRepository.findAll()).thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void getPageableTaxRates_ShouldReturnTaxRateListGetVm() {
        Page<TaxRate> page = new PageImpl<>(List.of(taxRate), PageRequest.of(0, 10), 1);
        when(taxRateRepository.findAll(any(Pageable.class))).thenReturn(page);

        // StateOrProvinceAndCountryGetNameVm(Long stateOrProvinceId, String stateOrProvinceName, String countryName)
        StateOrProvinceAndCountryGetNameVm locationVm =
                new StateOrProvinceAndCountryGetNameVm(1L, "California", "USA");
        when(locationService.getStateOrProvinceAndCountryNames(any())).thenReturn(List.of(locationVm));

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 10);

        // TaxRateListGetVm uses taxRateGetDetailContent()
        assertThat(result.taxRateGetDetailContent()).hasSize(1);
        assertThat(result.taxRateGetDetailContent().get(0).stateOrProvinceName()).isEqualTo("California");
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void getTaxPercent_ShouldReturnPercent_WhenFound() {
        when(taxRateRepository.getTaxPercent(1L, 1L, "12345", 1L)).thenReturn(10.0);

        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "12345");

        assertThat(result).isEqualTo(10.0);
    }

    @Test
    void getTaxPercent_ShouldReturnZero_WhenNotFound() {
        when(taxRateRepository.getTaxPercent(1L, 1L, "12345", 1L)).thenReturn(null);

        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "12345");

        assertThat(result).isEqualTo(0.0);
    }

    @Test
    void getBulkTaxRate_ShouldReturnListOfTaxRateVm() {
        when(taxRateRepository.getBatchTaxRates(eq(1L), eq(1L), eq("12345"), any(Set.class)))
                .thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.getBulkTaxRate(List.of(1L), 1L, 1L, "12345");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }
}
