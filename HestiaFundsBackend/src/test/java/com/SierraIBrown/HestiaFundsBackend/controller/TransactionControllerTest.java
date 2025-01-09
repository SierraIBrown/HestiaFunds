package com.SierraIBrown.HestiaFundsBackend.controller;

import com.SierraIBrown.HestiaFundsBackend.model.Category;
import com.SierraIBrown.HestiaFundsBackend.model.Transaction;
import com.SierraIBrown.HestiaFundsBackend.model.TransactionTest;
import com.SierraIBrown.HestiaFundsBackend.repository.CategoryRepository;
import com.SierraIBrown.HestiaFundsBackend.repository.TransactionRepository;
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
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Category savedCategory;

    private Category cat;

    @BeforeEach
    void setUp(){
        categoryRepository.deleteAll();
        transactionRepository.deleteAll();

        //Create valid category to use in tests
        cat = new Category("Test Category", false);
        savedCategory = categoryRepository.save(cat);
    }

    /*
    Tests getting all transactions when it is initially empty
     */
    @Test
    void testGetAllTransactions_InitiallyEmpty() throws Exception{
        //Expect an empty array since nothing has been created
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

    }

    /*
    Tests getting all transactions in a single month
     */
    @Test
    void testGetTransactionsByMonthAndYear() throws Exception{

        Transaction transaction1 = new Transaction();
        transaction1.setDate(LocalDate.of(2025, 1, 10));
        transaction1.setAmount(BigDecimal.valueOf(100.00));
        transaction1.setDescription("Walmart");
        transaction1.setCategory(cat);

        Transaction transaction2 = new Transaction();
        transaction2.setDate(LocalDate.of(2025, 1, 15));
        transaction2.setAmount(BigDecimal.valueOf(50.00));
        transaction2.setDescription("Gas Station");
        transaction2.setCategory(cat);

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);

        mockMvc.perform(get("/api/transactions/month/1/year/2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].date").value("2025-01-10"))
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[0].description").value("Walmart"))
                .andExpect(jsonPath("$[0].category.name").value("Test Category"))
                .andExpect(jsonPath("$[1].date").value("2025-01-15"))
                .andExpect(jsonPath("$[1].amount").value(50.0))
                .andExpect(jsonPath("$[1].description").value("Gas Station"))
                .andExpect(jsonPath("$[1].category.name").value("Test Category"));
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
