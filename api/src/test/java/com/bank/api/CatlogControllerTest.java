package com.bank.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bank.api.controller.CatalogController;
import com.bank.api.dto.ProductSummary;
import com.bank.api.dto.TopSellerView;
import com.bank.api.model.Product;
import com.bank.api.services.CatalogService;


@WebMvcTest(CatalogController.class)
public class CatlogControllerTest {
	
	
	 // MockMvc simulates HTTP requests to the controller
    @Autowired
    private MockMvc mockMvc;

    // Mock dependencies (the controller depends on these)
    @MockitoBean
    private CatalogService catalog;
    
    
    @Test
    void shouldReturnProductSummaries() throws Exception {
        Page<ProductSummary> page = new PageImpl<>(List.of(new ProductSummary(1L,"iPhone",new BigDecimal("999.00"))));
        when(catalog.listProductSummaries(anyInt(), anyInt(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/catalog/summaries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("iPhone"));
    }
    
    
    @Test
    void shouldSearchProducts() throws Exception {
        Page<Product> page = new PageImpl<>(List.of(
        		new Product("iPhone", "phones", new BigDecimal("999.00"))
        ));

        when(catalog.search(any(), any(), any(), anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/catalog/search").param("category", "phones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("iPhone"));
    }

    @Test
    void shouldReturnTopSellers() throws Exception {
    	TopSellerView top1 = new TopSellerView() {
            @Override public Long getProductId() { return 1L; }
            @Override public String getProductName() { return "iPhone"; }
            @Override public Long getTotalQty() { return 1L; }
        };

        TopSellerView top2 = new TopSellerView() {
            @Override public Long getProductId() { return 2L; }
            @Override public String getProductName() { return "Pixel"; }
            @Override public Long getTotalQty() { return 1L; }
        };

        List<TopSellerView> topSellers = List.of(top1, top2);
        when(catalog.topSellers(5)).thenReturn(topSellers);

        mockMvc.perform(get("/api/catalog/top-sellers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("iPhone"));
    }

}
