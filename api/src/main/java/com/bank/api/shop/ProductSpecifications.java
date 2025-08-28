package com.bank.api.shop;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.bank.api.model.Product;

public class ProductSpecifications {

	
	public static Specification<Product> activeOnly() {
	    return (root, q, cb) -> cb.isTrue(root.get("active"));
	  }
	  public static Specification<Product> categoryEquals(String cat) {
	    return (root, q, cb) -> cat == null ? cb.conjunction() : cb.equal(root.get("category"), cat);
	  }
	  public static Specification<Product> priceGte(BigDecimal min) {
	    return (root, q, cb) -> min == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("price"), min);
	  }
	  public static Specification<Product> priceLte(BigDecimal max) {
	    return (root, q, cb) -> max == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("price"), max);
	  }
	  
}
