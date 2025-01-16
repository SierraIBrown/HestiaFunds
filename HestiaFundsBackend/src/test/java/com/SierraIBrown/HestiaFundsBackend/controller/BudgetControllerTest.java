package com.SierraIBrown.HestiaFundsBackend.controller;

import com.SierraIBrown.HestiaFundsBackend.model.Budget;
import com.SierraIBrown.HestiaFundsBackend.model.Category;
import com.SierraIBrown.HestiaFundsBackend.repository.BudgetRepository;
import com.SierraIBrown.HestiaFundsBackend.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    public void setUp(){
        budgetRepository.deleteAll();
        categoryRepository.deleteAll();

        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory.setColor("#FFFFFF");
        categoryRepository.save(testCategory);
    }

    /*
    Tests getting a budget by id
     */
    @Test
    public void testGetBudgetById() throws Exception{
        Budget budget = new Budget();
        budget.setCategory(testCategory);
        budget.setAmount(new BigDecimal("500"));
        budget.setPeriodStart(LocalDate.of(2025, 1, 1));
        budget.setPeriodEnd(LocalDate.of(2025, 1, 31));
        budget = budgetRepository.save(budget);

        mockMvc.perform(get("/api/budgets/" + budget.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(500));
    }

    /*
    Tests creating a budget
     */
    @Test
    public void testCreateBudget() throws Exception{
        String budgetJson = String.format(
                """
                     {
                       "category": {"id": %d},
                       "amount": 500,
                       "periodStart": "2025-01-01",
                       "periodEnd": "2025-01-31"
                    } 
                """, testCategory.getId()
        );

        mockMvc.perform(post("/api/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(budgetJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(500));
    }

    /*
    Tests updating budget information
     */
    @Test
    public void testUpdateBudget() throws Exception{
        Budget budget = new Budget();
        budget.setCategory(testCategory);
        budget.setAmount(new BigDecimal("500"));
        budget.setPeriodStart(LocalDate.of(2025, 1, 1));
        budget.setPeriodEnd(LocalDate.of(2025, 1, 31));
        budget = budgetRepository.save(budget);

        String updatedBudgetJson = String.format(
                """
                      {
                        "amount": 600,
                        "periodEnd": "2025-02-01"
                      }
                """
        );

        mockMvc.perform(put("/api/budgets/" + budget.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedBudgetJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(600))
                .andExpect(jsonPath("$.periodEnd").value("2025-02-01"));
    }

    /*
    Tests deleting a budget
     */
    @Test
    public void testDeleteBudget() throws Exception{
        Budget budget = new Budget();
        budget.setCategory(testCategory);
        budget.setAmount(new BigDecimal("500"));
        budget.setPeriodStart(LocalDate.of(2025, 1, 1));
        budget.setPeriodEnd(LocalDate.of(2025, 1, 31));
        budget = budgetRepository.save(budget);

        mockMvc.perform(delete("/api/budgets/" + budget.getId()))
                .andExpect(status().isNoContent());
    }
}
