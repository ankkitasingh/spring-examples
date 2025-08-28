package com.bank.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bank.api.model.Order;

public interface OrderRepository extends JpaRepository<Order,Long>{
	
	
	@Query("""
		      select distinct o from Order o
		      left join fetch o.items i
		      left join fetch i.product p
		      where o.user.id = :userId
		      order by o.createdAt desc
		      """)
	List<Order> findOrdersWithItemsForUser(@Param("userId") Long userId);

}
