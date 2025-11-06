package com.bank.api;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bank.api.controller.BankController;
import com.bank.api.model.Account;
import com.bank.api.repository.AccountRepository;
import com.bank.api.services.BankService;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(BankController.class)
public class BankControllerTest {

	 // MockMvc simulates HTTP requests to the controller
    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper helps convert objects to JSON
    @Autowired
    private ObjectMapper objectMapper;

    // Mock dependencies (the controller depends on these)
    @MockitoBean
    private BankService bank;

    @MockitoBean
    private AccountRepository accounts;

    @Test
    void shouldCreateAccount() throws Exception {
        // Arrange: define how the mock repository behaves
        Account saved = new Account("Alice", new BigDecimal("1000.00"));
        when(accounts.save(any(Account.class))).thenReturn(saved);

        // Act: send a simulated POST request
        Map<String, String> req = Map.of("owner", "Alice", "balance", "1000.00");

        mockMvc.perform(post("/api/bank/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                // Assert: verify status and JSON response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owner").value("Alice"));
    }

    @Test
    void shouldTransferFunds() throws Exception {
        // Arrange: request data
        Map<String, String> req = Map.of("fromId", "1", "toId", "2", "amount", "50.00");

        // Act: send request
        mockMvc.perform(post("/api/bank/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));

        // Verify that the service was called with correct parameters
        verify(bank).transfer(1L, 2L, new BigDecimal("50.00"), false);
    }
}
