package com.SierraIBrown.HestiaFundsBackend.controller;

import com.SierraIBrown.HestiaFundsBackend.model.Category;
import com.SierraIBrown.HestiaFundsBackend.model.Transaction;
import com.SierraIBrown.HestiaFundsBackend.model.TransactionTest;
import com.SierraIBrown.HestiaFundsBackend.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Category savedCategory;

    @BeforeEach
    void setUp(){
        categoryRepository.deleteAll();

        //Create valid category to use in tests
        Category cat = new Category("Test Category", false);
        savedCategory = categoryRepository.save(cat);
    }

    @Test
    void testGetAllTransactions_InitiallyEmpty() throws Exception{
        //Expect an empty array since nothing has been created
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

    }

    @Test
    void testCreateTransaction_Success() throws Exception {
        //Build JSON for transaction
        Transaction tx = new Transaction();
        tx.setAmount(BigDecimal.valueOf(25.75));
        tx.setDate(LocalDate.of(2025, 1, 5));
        tx.setDescription("Subscription to Xbox Game Pass Ultimate");
        tx.setCategory(savedCategory);

        //Convert transaction object to JSON
        String requestBody = objectMapper.writeValueAsString(tx);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.amount").value(25.75))
                .andExpect(jsonPath("$.description").value("Subscription to Xbox Game Pass Ultimate"));

    }

    @Test
    void testCreateTransaction_InvalidCategory() throws Exception{
        Transaction tx = new Transaction();
        tx.setAmount(BigDecimal.valueOf(10.00));
        tx.setDate(LocalDate.of(2025, 1, 5));
        Category fakeCat = new Category();
        fakeCat.setId(9999L);
        tx.setCategory((fakeCat));

        String requestBody = objectMapper.writeValueAsString(tx);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid Category: 9999"));
    }

    @Test
    void testCreateTransaction_MissingFields() throws Exception {
        Transaction tx = new Transaction();
        String requestBody = objectMapper.writeValueAsString(tx);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount cannot be null."));
    }

    @Test
    void testGetAllTransactions_AfterCreation() throws Exception{
        Transaction tx = new Transaction();
        tx.setAmount(BigDecimal.valueOf(12.50));
        tx.setDate(LocalDate.of(2025, 2, 1));
        tx.setDescription("Some expense");
        tx.setCategory(savedCategory);

        String requestBody = objectMapper.writeValueAsString(tx);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testUpdateTransaction() throws Exception{
        Transaction tx = new Transaction();
        tx.setAmount(BigDecimal.valueOf(20.00));
        tx.setDate(LocalDate.now());
        tx.setDescription("Before update");
        tx.setCategory(savedCategory);

        String createRequest = objectMapper.writeValueAsString(tx);
        String createResponse = mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //Parse
        Transaction createdTx = objectMapper.readValue(createResponse, Transaction.class);

        createdTx.setAmount(BigDecimal.valueOf(35.00));
        createdTx.setDescription("After update");

        String updateRequest = objectMapper.writeValueAsString(createdTx);

        mockMvc.perform(put("/api/transactions/{id}", createdTx.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdTx.getId()))
                .andExpect(jsonPath("$.amount").value(35.00))
                .andExpect(jsonPath("$.description").value("After update"));
    }

    @Test
    void testDeleteTransaction() throws Exception{
        Transaction tx = new Transaction();
        tx.setAmount(BigDecimal.valueOf(100.00));
        tx.setDate(LocalDate.now());
        tx.setDescription("Delete me");
        tx.setCategory(savedCategory);

        String createRequest = objectMapper.writeValueAsString(tx);

        String createResponse = mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Transaction createdTx = objectMapper.readValue(createResponse, Transaction.class);

        mockMvc.perform(delete("/api/transactions/{id}", createdTx.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/transactions/{id}", createdTx.getId()))
                .andExpect(status().isNotFound());
    }
}
