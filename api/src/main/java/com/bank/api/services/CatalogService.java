package com.bank.api.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.bank.api.dto.ProductSummary;
import com.bank.api.dto.TopSellerView;
import com.bank.api.model.Product;
import com.bank.api.repository.ProductRepository;
import com.bank.api.shop.ProductSpecifications;

@Service
public class CatalogService {
	
	private final ProductRepository products;
	  public CatalogService(ProductRepository products) { this.products = products; }

	  public Page<ProductSummary> listProductSummaries(Integer page, Integer size, String sort, String category) {
	    Pageable pageable = PageRequest.of(page, size, Sort.by(sort == null ? "price" : sort));
	    return products.findSummaries(category, pageable);
	  }

	  public Page<Product> search(String category, BigDecimal min, BigDecimal max, int page, int size) {
	    Specification<Product> spec = Specification
	        .where(ProductSpecifications.activeOnly())
	        .and(ProductSpecifications.categoryEquals(category))
	        .and(ProductSpecifications.priceGte(min))
	        .and(ProductSpecifications.priceLte(max));
	    return products.findAll(spec, PageRequest.of(page, size));
	  }

	  public List<TopSellerView> topSellers(int limit) {
	    return products.topSellers(limit);
	  }

}
