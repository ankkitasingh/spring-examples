package com.product.catalogue.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.product.catalogue.dto.ProductRequest;
import com.product.catalogue.dto.ProductResponse;
import com.product.catalogue.entity.Product;
import com.product.catalogue.repository.ProductRepository;
import com.product.catalogue.service.ProductService;


@Service
public class ProductServiceImpl implements ProductService {
	
	
	private final ProductRepository productRepository;

  
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getPrice()))
                .collect(Collectors.toList());
    }
    
    @Override
    public ProductResponse getProductById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return new ProductResponse(p.getId(), p.getName(), p.getPrice());
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        Product saved = productRepository.save(product);
        return new ProductResponse(saved.getId(), saved.getName(), saved.getPrice());
    }


    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product updated = productRepository.findById(id)
                .map(p -> {
                    p.setName(request.getName());
                    p.setDescription(request.getDescription());
                    p.setPrice(request.getPrice());
                    return productRepository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return new ProductResponse(updated.getId(), updated.getName(), updated.getPrice());
    }
    
    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
