package com.bank.api.repository;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.bank.api.dto.ProductSummary;
import com.bank.api.dto.TopSellerView;
import com.bank.api.model.Product;

public interface ProductRepository extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product>{
	
	
	@Query("""
		     select new com.bank.api.dto.ProductSummary(p.id, p.name, p.price)
		     from Product p
		     where p.active = true and (:cat is null or p.category = :cat)
		     """)
		  Page<ProductSummary> findSummaries(@Param("cat") String category, Pageable pageable);

		  Page<Product> findByActiveTrue(Pageable pageable);

		  // Postgres native + interface projection
		  @Query(value = """
		      select p.id as productId, p.name as productName, sum(oi.quantity) as totalQty
		      from order_item oi
		      join productss p on p.id = oi.product_id
		      group by p.id, p.name
		      order by totalQty desc
		      limit :limit
		      """, nativeQuery = true)
		  
		  List<TopSellerView> topSellers(@Param("limit") int limit);

}
