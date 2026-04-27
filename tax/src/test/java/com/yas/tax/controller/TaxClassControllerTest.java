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
import com.yas.tax.service.TaxClassService;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
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
public class TaxClassControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private TaxClassService taxClassService;

    @InjectMocks
    private TaxClassController taxClassController;

    private TaxClassVm taxClassVm;
    private TaxClassPostVm taxClassPostVm;
    private TaxClass taxClass;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taxClassController).build();
        objectMapper = new ObjectMapper();

        // TaxClassVm(Long id, String name)
        taxClassVm = new TaxClassVm(1L, "Standard");
        // TaxClassPostVm(String id, String name)
        taxClassPostVm = new TaxClassPostVm("1", "Standard");
        taxClass = TaxClass.builder()
                .id(1L)
                .name("Standard")
                .build();
    }

    @Test
    void getPageableTaxClasses_ShouldReturnOk() throws Exception {
        // TaxClassListGetVm uses taxClassContent()
        TaxClassListGetVm listGetVm = new TaxClassListGetVm(List.of(taxClassVm), 0, 10, 1, 1, true);
        when(taxClassService.getPageableTaxClasses(anyInt(), anyInt())).thenReturn(listGetVm);

        mockMvc.perform(get("/backoffice/tax-classes/paging")
                        .param("pageNo", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void listTaxClasses_ShouldReturnOk() throws Exception {
        when(taxClassService.findAllTaxClasses()).thenReturn(List.of(taxClassVm));

        mockMvc.perform(get("/backoffice/tax-classes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Standard"));
    }

    @Test
    void getTaxClass_ShouldReturnOk() throws Exception {
        when(taxClassService.findById(1L)).thenReturn(taxClassVm);

        mockMvc.perform(get("/backoffice/tax-classes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Standard"));
    }

    @Test
    void createTaxClass_ShouldReturnCreated() throws Exception {
        when(taxClassService.create(any(TaxClassPostVm.class))).thenReturn(taxClass);

        mockMvc.perform(post("/backoffice/tax-classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taxClassPostVm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateTaxClass_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(put("/backoffice/tax-classes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taxClassPostVm)))
                .andExpect(status().isNoContent());

        verify(taxClassService).update(any(TaxClassPostVm.class), eq(1L));
    }

    @Test
    void deleteTaxClass_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/backoffice/tax-classes/1"))
                .andExpect(status().isNoContent());

        verify(taxClassService).delete(1L);
    }
}
