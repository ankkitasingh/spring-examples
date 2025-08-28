package com.bank.api.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.api.dto.ProductSummary;
import com.bank.api.dto.TopSellerView;
import com.bank.api.model.Product;
import com.bank.api.services.CatalogService;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {
  private final CatalogService catalog;
  public CatalogController(CatalogService catalog) { this.catalog = catalog; }

  @GetMapping("/summaries")
  public Page<ProductSummary> summaries(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) String category
  ) {
    return catalog.listProductSummaries(page, size, sort, category);
  }

  @GetMapping("/search")
  public Page<Product> search(
      @RequestParam(required = false) String category,
      @RequestParam(required = false) BigDecimal min,
      @RequestParam(required = false) BigDecimal max,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return catalog.search(category, min, max, page, size);
  }

  @GetMapping("/top-sellers")
  public List<TopSellerView> topSellers(@RequestParam(defaultValue = "5") int limit) {
    return catalog.topSellers(limit);
  }
}
