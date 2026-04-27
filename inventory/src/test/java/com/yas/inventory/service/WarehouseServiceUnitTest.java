package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.address.AddressDetailVm;
import com.yas.inventory.viewmodel.address.AddressPostVm;
import com.yas.inventory.viewmodel.address.AddressVm;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseDetailVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehousePostVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceUnitTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductService productService;

    @Mock
    private LocationService locationService;

    private WarehouseService warehouseService;

    @BeforeEach
    void setUp() {
        warehouseService = new WarehouseService(warehouseRepository, stockRepository, productService, locationService);
    }

    @Test
    void findAllWarehouses_shouldMapFromModel() {
        when(warehouseRepository.findAll()).thenReturn(List.of(
            Warehouse.builder().id(1L).name("W1").addressId(10L).build(),
            Warehouse.builder().id(2L).name("W2").addressId(11L).build()
        ));

        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

        assertEquals(2, result.size());
        assertEquals("W1", result.getFirst().name());
    }

    @Test
    void getProductWarehouse_whenWarehouseHasProducts_shouldMarkExistingFlag() {
        List<ProductInfoVm> filteredProducts = List.of(
            new ProductInfoVm(11L, "P1", "SKU1", false),
            new ProductInfoVm(12L, "P2", "SKU2", false)
        );

        when(stockRepository.getProductIdsInWarehouse(7L)).thenReturn(List.of(11L));
        when(productService.filterProducts("P", "SKU", List.of(11L), FilterExistInWhSelection.YES))
            .thenReturn(filteredProducts);

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(7L, "P", "SKU", FilterExistInWhSelection.YES);

        assertEquals(2, result.size());
        assertEquals(true, result.get(0).existInWh());
        assertEquals(false, result.get(1).existInWh());
    }

    @Test
    void getProductWarehouse_whenNoProductInWarehouse_shouldReturnOriginalList() {
        List<ProductInfoVm> filteredProducts = List.of(new ProductInfoVm(11L, "P1", "SKU1", false));

        when(stockRepository.getProductIdsInWarehouse(7L)).thenReturn(List.of());
        when(productService.filterProducts("P", "SKU", List.of(), FilterExistInWhSelection.NO))
            .thenReturn(filteredProducts);

        List<ProductInfoVm> result = warehouseService.getProductWarehouse(7L, "P", "SKU", FilterExistInWhSelection.NO);

        assertSame(filteredProducts, result);
    }

    @Test
    void findById_whenWarehouseNotFound_shouldThrow() {
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.findById(99L));
    }

    @Test
    void findById_whenWarehouseFound_shouldReturnDetailVm() {
        Warehouse warehouse = Warehouse.builder().id(5L).name("WH5").addressId(66L).build();
        AddressDetailVm addressDetailVm = AddressDetailVm.builder()
            .id(66L)
            .contactName("John")
            .phone("0123")
            .addressLine1("A1")
            .addressLine2("A2")
            .city("HCM")
            .zipCode("70000")
            .districtId(1L)
            .stateOrProvinceId(2L)
            .countryId(3L)
            .build();

        when(warehouseRepository.findById(5L)).thenReturn(Optional.of(warehouse));
        when(locationService.getAddressById(66L)).thenReturn(addressDetailVm);

        WarehouseDetailVm result = warehouseService.findById(5L);

        assertEquals(5L, result.id());
        assertEquals("John", result.contactName());
    }

    @Test
    void create_whenNameDuplicated_shouldThrow() {
        WarehousePostVm postVm = WarehousePostVm.builder().name("WH1").build();
        when(warehouseRepository.existsByName("WH1")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.create(postVm));
    }

    @Test
    void create_whenValidInput_shouldSaveWarehouse() {
        WarehousePostVm postVm = WarehousePostVm.builder()
            .name("WH1")
            .contactName("John")
            .phone("0123")
            .addressLine1("A1")
            .addressLine2("A2")
            .city("HCM")
            .zipCode("70000")
            .districtId(1L)
            .stateOrProvinceId(2L)
            .countryId(3L)
            .build();

        when(warehouseRepository.existsByName("WH1")).thenReturn(false);
        when(locationService.createAddress(any(AddressPostVm.class))).thenReturn(
            AddressVm.builder().id(77L).build()
        );
        when(warehouseRepository.save(any(Warehouse.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Warehouse result = warehouseService.create(postVm);

        assertEquals("WH1", result.getName());
        assertEquals(77L, result.getAddressId());
    }

    @Test
    void update_whenValidInput_shouldUpdateWarehouseAndAddress() {
        Warehouse warehouse = Warehouse.builder().id(5L).name("old").addressId(66L).build();
        WarehousePostVm postVm = WarehousePostVm.builder()
            .name("new")
            .contactName("John")
            .phone("0123")
            .addressLine1("A1")
            .addressLine2("A2")
            .city("HCM")
            .zipCode("70000")
            .districtId(1L)
            .stateOrProvinceId(2L)
            .countryId(3L)
            .build();

        when(warehouseRepository.findById(5L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameWithDifferentId("new", 5L)).thenReturn(false);

        warehouseService.update(postVm, 5L);

        ArgumentCaptor<Warehouse> captor = ArgumentCaptor.forClass(Warehouse.class);
        verify(warehouseRepository).save(captor.capture());
        verify(locationService).updateAddress(any(Long.class), any(AddressPostVm.class));
        assertEquals("new", captor.getValue().getName());
    }

    @Test
    void update_whenWarehouseNotFound_shouldThrow() {
        WarehousePostVm postVm = WarehousePostVm.builder().name("new").build();
        when(warehouseRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.update(postVm, 5L));
    }

    @Test
    void update_whenNameDuplicated_shouldThrow() {
        Warehouse warehouse = Warehouse.builder().id(5L).name("old").addressId(66L).build();
        WarehousePostVm postVm = WarehousePostVm.builder().name("new").build();

        when(warehouseRepository.findById(5L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameWithDifferentId("new", 5L)).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.update(postVm, 5L));
    }

    @Test
    void delete_whenWarehouseFound_shouldDeleteWarehouseAndAddress() {
        Warehouse warehouse = Warehouse.builder().id(5L).name("W5").addressId(66L).build();
        when(warehouseRepository.findById(5L)).thenReturn(Optional.of(warehouse));

        warehouseService.delete(5L);

        verify(warehouseRepository).deleteById(5L);
        verify(locationService).deleteAddress(66L);
    }

    @Test
    void delete_whenWarehouseNotFound_shouldThrow() {
        when(warehouseRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.delete(5L));
    }

    @Test
    void getPageableWarehouses_shouldReturnPagingInfo() {
        List<Warehouse> content = List.of(Warehouse.builder().id(1L).name("W1").addressId(10L).build());
        Page<Warehouse> page = new PageImpl<>(content, PageRequest.of(0, 10), 11);
        when(warehouseRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        var result = warehouseService.getPageableWarehouses(0, 10);

        assertEquals(1, result.warehouseContent().size());
        assertEquals(0, result.pageNo());
        assertEquals(10, result.pageSize());
        assertEquals(11, result.totalElements());
    }
}