package com.yas.tax.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.tax.model.TaxClass;
import com.yas.tax.service.TaxRateService;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import com.yas.tax.model.TaxRate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class TaxRateControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private TaxRateService taxRateService;

    @InjectMocks
    private TaxRateController taxRateController;

    private TaxRateVm taxRateVm;
    private TaxRatePostVm taxRatePostVm;
    private TaxRate taxRate;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taxRateController).build();
        objectMapper = new ObjectMapper();

        // TaxRateVm(Long id, Double rate, String zipCode, Long taxClassId, Long stateOrProvinceId, Long countryId)
        taxRateVm = new TaxRateVm(1L, 10.0, "12345", 1L, 1L, 1L);
        // TaxRatePostVm(Double rate, String zipCode, Long taxClassId, Long stateOrProvinceId, Long countryId)
        taxRatePostVm = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);

        TaxClass taxClass = TaxClass.builder()
                .id(1L)
                .name("Standard")
                .build();

        taxRate = TaxRate.builder()
                .id(1L)
                .rate(10.0)
                .zipCode("12345")
                .taxClass(taxClass)
                .stateOrProvinceId(1L)
                .countryId(1L)
                .build();
    }

    @Test
    void getPageableTaxRates_ShouldReturnOk() throws Exception {
        TaxRateListGetVm listGetVm = new TaxRateListGetVm(List.of(), 0, 10, 0, 0, true);
        when(taxRateService.getPageableTaxRates(anyInt(), anyInt())).thenReturn(listGetVm);

        mockMvc.perform(get("/backoffice/tax-rates/paging")
                        .param("pageNo", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getTaxRate_ShouldReturnOk() throws Exception {
        when(taxRateService.findById(1L)).thenReturn(taxRateVm);

        mockMvc.perform(get("/backoffice/tax-rates/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.rate").value(10.0));
    }

    @Test
    void createTaxRate_ShouldReturnCreated() throws Exception {
        when(taxRateService.createTaxRate(any(TaxRatePostVm.class))).thenReturn(taxRate);

        mockMvc.perform(post("/backoffice/tax-rates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taxRatePostVm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateTaxRate_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(put("/backoffice/tax-rates/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taxRatePostVm)))
                .andExpect(status().isNoContent());

        verify(taxRateService).updateTaxRate(any(TaxRatePostVm.class), eq(1L));
    }

    @Test
    void deleteTaxRate_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/backoffice/tax-rates/1"))
                .andExpect(status().isNoContent());

        verify(taxRateService).delete(1L);
    }

    @Test
    void getTaxPercentByAddress_ShouldReturnOk() throws Exception {
        when(taxRateService.getTaxPercent(1L, 1L, 1L, "12345")).thenReturn(10.0);

        mockMvc.perform(get("/backoffice/tax-rates/tax-percent")
                        .param("taxClassId", "1")
                        .param("countryId", "1")
                        .param("stateOrProvinceId", "1")
                        .param("zipCode", "12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(10.0));
    }

    @Test
    void getBatchTaxPercentsByAddress_ShouldReturnOk() throws Exception {
        when(taxRateService.getBulkTaxRate(List.of(1L), 1L, 1L, "12345")).thenReturn(List.of(taxRateVm));

        mockMvc.perform(get("/backoffice/tax-rates/location-based-batch")
                        .param("taxClassIds", "1")
                        .param("countryId", "1")
                        .param("stateOrProvinceId", "1")
                        .param("zipCode", "12345"))
                .andExpect(status().isOk());
    }
}
