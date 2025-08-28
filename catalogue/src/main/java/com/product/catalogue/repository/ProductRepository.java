package com.product.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.product.catalogue.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
