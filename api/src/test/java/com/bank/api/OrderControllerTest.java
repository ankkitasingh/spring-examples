package com.bank.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import com.bank.api.controller.OrderController;
import com.bank.api.model.Order;
import com.bank.api.model.OrderItem;
import com.bank.api.model.Product;
import com.bank.api.model.User;
import com.bank.api.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
		controllers = OrderController.class,
		excludeAutoConfiguration = {
		        DataSourceAutoConfiguration.class,
		        HibernateJpaAutoConfiguration.class,
		        JpaRepositoriesAutoConfiguration.class
		    }
		)
public class OrderControllerTest {
	
	
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orders;

    @Test
    void shouldCreateOrder() throws Exception {
        when(orders.createOrder(eq("user@example.com"), anyList())).thenReturn(42L);

        Map<String, Object> body = Map.of(
                "email", "user@example.com",
                "productIds", List.of(1, 2, 3)
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(42));
    }

    @Test
    void shouldReturnOrdersByUser() throws Exception {
        // Arrange — create sample entities
    	User user = new User("alice@example.com", "Alice");
        setField(user, "id", 1L);

        Product iphone = new Product("iPhone", "phones", new BigDecimal("999.00"));
        setField(iphone, "id", 101L);

        Product thinkPad = new Product("ThinkPad", "laptops", new BigDecimal("1599.00"));
        setField(thinkPad, "id", 102L);

        Order order = new Order(user);
        setField(order, "id", 1001L);

        OrderItem item1 = new OrderItem(iphone, 1, new BigDecimal("1200.00"));
        OrderItem item2 = new OrderItem(thinkPad, 2, new BigDecimal("800.00"));

        order.addItem(item1);
        order.addItem(item2);

        when(orders.getOrdersForUser(1L)).thenReturn(List.of(order));

        // Act & Assert
        mockMvc.perform(get("/api/orders/user/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1001))
                .andExpect(jsonPath("$[0].user.email").value("alice@example.com"))
                .andExpect(jsonPath("$[0].items[0].product.name").value("iPhone"))
                .andExpect(jsonPath("$[0].items[0].quantity").value(1))
                .andExpect(jsonPath("$[0].items[1].product.name").value("ThinkPad"))
                .andExpect(jsonPath("$[0].items[1].quantity").value(2));
    }

    // Utility: safely set private fields like IDs
    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
