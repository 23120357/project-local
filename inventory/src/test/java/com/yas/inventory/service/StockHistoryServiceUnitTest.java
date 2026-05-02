package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.inventory.model.Stock;
import com.yas.inventory.model.StockHistory;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockHistoryRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stockhistory.StockHistoryListVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockHistoryServiceUnitTest {

    @Mock
    private StockHistoryRepository stockHistoryRepository;

    @Mock
    private ProductService productService;

    private StockHistoryService stockHistoryService;

    @BeforeEach
    void setUp() {
        stockHistoryService = new StockHistoryService(stockHistoryRepository, productService);
    }

    @Test
    void createStockHistories_shouldCreateOnlyMatchedHistoryRows() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("W1").addressId(10L).build();
        Stock stock1 = Stock.builder().id(100L).productId(10L).warehouse(warehouse).quantity(1L).build();
        Stock stock2 = Stock.builder().id(101L).productId(20L).warehouse(warehouse).quantity(2L).build();

        List<StockQuantityVm> requests = List.of(new StockQuantityVm(101L, -1L, "adjust"));

        stockHistoryService.createStockHistories(List.of(stock1, stock2), requests);

        ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
        verify(stockHistoryRepository).saveAll(captor.capture());

        List<StockHistory> saved = captor.getValue();
        assertEquals(1, saved.size());
        assertEquals(20L, saved.getFirst().getProductId());
        assertEquals(-1L, saved.getFirst().getAdjustedQuantity());
        assertEquals("adjust", saved.getFirst().getNote());
    }

    @Test
    void createStockHistories_whenNoRequestMatched_shouldSaveEmptyList() {
        Warehouse warehouse = Warehouse.builder().id(1L).name("W1").addressId(10L).build();
        Stock stock = Stock.builder().id(100L).productId(10L).warehouse(warehouse).quantity(1L).build();

        List<StockQuantityVm> requests = List.of(new StockQuantityVm(999L, 1L, "none"));

        stockHistoryService.createStockHistories(List.of(stock), requests);

        ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
        verify(stockHistoryRepository).saveAll(captor.capture());
        assertEquals(0, captor.getValue().size());
    }

    @Test
    void getStockHistories_shouldMapWithProductName() {
        StockHistory history = StockHistory.builder()
            .id(11L)
            .productId(7L)
            .adjustedQuantity(5L)
            .note("note")
            .build();

        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(7L, 9L))
            .thenReturn(List.of(history));
        when(productService.getProduct(7L)).thenReturn(new ProductInfoVm(7L, "P7", "SKU7", true));

        StockHistoryListVm result = stockHistoryService.getStockHistories(7L, 9L);

        assertEquals(1, result.data().size());
        assertEquals(11L, result.data().getFirst().id());
        assertEquals("P7", result.data().getFirst().productName());
        assertEquals(5L, result.data().getFirst().adjustedQuantity());
    }
}