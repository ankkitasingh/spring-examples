package com.product.catalogue.service;

import java.util.List;

import com.product.catalogue.dto.ProductRequest;
import com.product.catalogue.dto.ProductResponse;


public interface ProductService {
	
	List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);

}
