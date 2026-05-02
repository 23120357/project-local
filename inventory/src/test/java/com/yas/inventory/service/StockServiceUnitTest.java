package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.StockExistingException;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.inventory.model.Stock;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.product.ProductQuantityPostVm;
import com.yas.inventory.viewmodel.stock.StockPostVm;
import com.yas.inventory.viewmodel.stock.StockQuantityUpdateVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stock.StockVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockServiceUnitTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductService productService;

    @Mock
    private WarehouseService warehouseService;

    @Mock
    private StockHistoryService stockHistoryService;

    private StockService stockService;

    @BeforeEach
    void setUp() {
        stockService = new StockService(
            warehouseRepository,
            stockRepository,
            productService,
            warehouseService,
            stockHistoryService
        );
    }

    @Test
    void addProductIntoWarehouse_whenValidInput_shouldSaveStocks() {
        Warehouse warehouse = Warehouse.builder().id(7L).name("WH-A").addressId(10L).build();
        List<StockPostVm> postVms = List.of(new StockPostVm(11L, 7L), new StockPostVm(12L, 7L));

        when(stockRepository.existsByWarehouseIdAndProductId(7L, 11L)).thenReturn(false);
        when(stockRepository.existsByWarehouseIdAndProductId(7L, 12L)).thenReturn(false);
        when(productService.getProduct(11L)).thenReturn(new ProductInfoVm(11L, "P1", "SKU1", true));
        when(productService.getProduct(12L)).thenReturn(new ProductInfoVm(12L, "P2", "SKU2", true));
        when(warehouseRepository.findById(7L)).thenReturn(Optional.of(warehouse));

        stockService.addProductIntoWarehouse(postVms);

        ArgumentCaptor<List<Stock>> captor = ArgumentCaptor.forClass(List.class);
        verify(stockRepository).saveAll(captor.capture());
        List<Stock> savedStocks = captor.getValue();

        assertEquals(2, savedStocks.size());
        assertEquals(0L, savedStocks.get(0).getQuantity());
        assertEquals(0L, savedStocks.get(0).getReservedQuantity());
        assertEquals(7L, savedStocks.get(0).getWarehouse().getId());
    }

    @Test
    void addProductIntoWarehouse_whenStockAlreadyExists_shouldThrow() {
        when(stockRepository.existsByWarehouseIdAndProductId(7L, 11L)).thenReturn(true);

        assertThrows(
            StockExistingException.class,
            () -> stockService.addProductIntoWarehouse(List.of(new StockPostVm(11L, 7L)))
        );

        verify(stockRepository, never()).saveAll(any());
    }

    @Test
    void addProductIntoWarehouse_whenProductNotFound_shouldThrow() {
        when(stockRepository.existsByWarehouseIdAndProductId(7L, 11L)).thenReturn(false);
        when(productService.getProduct(11L)).thenReturn(null);

        assertThrows(
            NotFoundException.class,
            () -> stockService.addProductIntoWarehouse(List.of(new StockPostVm(11L, 7L)))
        );

        verify(stockRepository, never()).saveAll(any());
    }

    @Test
    void addProductIntoWarehouse_whenWarehouseNotFound_shouldThrow() {
        when(stockRepository.existsByWarehouseIdAndProductId(7L, 11L)).thenReturn(false);
        when(productService.getProduct(11L)).thenReturn(new ProductInfoVm(11L, "P1", "SKU1", true));
        when(warehouseRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(
            NotFoundException.class,
            () -> stockService.addProductIntoWarehouse(List.of(new StockPostVm(11L, 7L)))
        );

        verify(stockRepository, never()).saveAll(any());
    }

    @Test
    void getStocksByWarehouseIdAndProductNameAndSku_whenValidInput_shouldMapResult() {
        ProductInfoVm p1 = new ProductInfoVm(11L, "P1", "SKU1", true);
        Warehouse warehouse = Warehouse.builder().id(9L).name("W9").addressId(1L).build();
        Stock stock = Stock.builder()
            .id(1L)
            .productId(11L)
            .quantity(15L)
            .reservedQuantity(2L)
            .warehouse(warehouse)
            .build();

        when(warehouseService.getProductWarehouse(9L, "P", "SKU", FilterExistInWhSelection.YES))
            .thenReturn(List.of(p1));
        when(stockRepository.findByWarehouseIdAndProductIdIn(9L, List.of(11L))).thenReturn(List.of(stock));

        List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(9L, "P", "SKU");

        assertEquals(1, result.size());
        assertEquals("P1", result.getFirst().productName());
        assertEquals("SKU1", result.getFirst().productSku());
        assertEquals(9L, result.getFirst().warehouseId());
    }

    @Test
    void getStocksByWarehouseIdAndProductNameAndSku_whenNoProducts_shouldReturnEmptyList() {
        when(warehouseService.getProductWarehouse(9L, "P", "SKU", FilterExistInWhSelection.YES))
            .thenReturn(List.of());
        when(stockRepository.findByWarehouseIdAndProductIdIn(9L, List.of())).thenReturn(List.of());

        List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(9L, "P", "SKU");

        assertEquals(0, result.size());
    }

    @Test
    void updateProductQuantityInStock_whenStocksExist_shouldUpdateAndPublish() {
        Warehouse warehouse = Warehouse.builder().id(9L).name("W9").addressId(1L).build();
        Stock stock1 = Stock.builder()
            .id(100L)
            .productId(11L)
            .quantity(10L)
            .reservedQuantity(0L)
            .warehouse(warehouse)
            .build();
        Stock stock2 = Stock.builder()
            .id(101L)
            .productId(12L)
            .quantity(20L)
            .reservedQuantity(0L)
            .warehouse(warehouse)
            .build();

        List<StockQuantityVm> requestItems = List.of(
            new StockQuantityVm(100L, 5L, "add stock"),
            new StockQuantityVm(101L, null, "no change")
        );
        StockQuantityUpdateVm request = new StockQuantityUpdateVm(requestItems);

        when(stockRepository.findAllById(List.of(100L, 101L))).thenReturn(List.of(stock1, stock2));

        stockService.updateProductQuantityInStock(request);

        assertEquals(15L, stock1.getQuantity());
        assertEquals(20L, stock2.getQuantity());
        verify(stockRepository).saveAll(List.of(stock1, stock2));
        verify(stockHistoryService).createStockHistories(List.of(stock1, stock2), requestItems);

        ArgumentCaptor<List<ProductQuantityPostVm>> productQtyCaptor = ArgumentCaptor.forClass(List.class);
        verify(productService).updateProductQuantity(productQtyCaptor.capture());
        assertEquals(2, productQtyCaptor.getValue().size());
    }

    @Test
    void updateProductQuantityInStock_whenNoStockFound_shouldNotUpdateProductQuantity() {
        List<StockQuantityVm> requestItems = List.of(new StockQuantityVm(100L, 5L, "add stock"));
        StockQuantityUpdateVm request = new StockQuantityUpdateVm(requestItems);

        when(stockRepository.findAllById(List.of(100L))).thenReturn(List.of());

        stockService.updateProductQuantityInStock(request);

        verify(stockRepository).saveAll(List.of());
        verify(stockHistoryService).createStockHistories(List.of(), requestItems);
        verify(productService, never()).updateProductQuantity(any());
    }

    @Test
    void updateProductQuantityInStock_whenRequestDoesNotMatchAnyStock_shouldSkipAdjustment() {
        Warehouse warehouse = Warehouse.builder().id(9L).name("W9").addressId(1L).build();
        Stock stock = Stock.builder()
            .id(200L)
            .productId(99L)
            .quantity(10L)
            .reservedQuantity(0L)
            .warehouse(warehouse)
            .build();
        List<StockQuantityVm> requestItems = List.of(new StockQuantityVm(201L, 3L, "no match"));
        StockQuantityUpdateVm request = new StockQuantityUpdateVm(requestItems);

        when(stockRepository.findAllById(List.of(201L))).thenReturn(List.of(stock));

        stockService.updateProductQuantityInStock(request);

        assertEquals(10L, stock.getQuantity());
        verify(stockRepository).saveAll(List.of(stock));
    }

    @Test
    void updateProductQuantityInStock_whenAdjustedQuantityInvalid_shouldThrow() {
        Warehouse warehouse = Warehouse.builder().id(9L).name("W9").addressId(1L).build();
        Stock stock = Stock.builder()
            .id(100L)
            .productId(11L)
            .quantity(-5L)
            .reservedQuantity(0L)
            .warehouse(warehouse)
            .build();

        List<StockQuantityVm> requestItems = List.of(new StockQuantityVm(100L, -1L, "invalid"));
        StockQuantityUpdateVm request = new StockQuantityUpdateVm(requestItems);

        when(stockRepository.findAllById(List.of(100L))).thenReturn(List.of(stock));

        assertThrows(BadRequestException.class, () -> stockService.updateProductQuantityInStock(request));
        verify(stockRepository, never()).saveAll(any());
        verify(stockHistoryService, never()).createStockHistories(any(), any());
    }
}