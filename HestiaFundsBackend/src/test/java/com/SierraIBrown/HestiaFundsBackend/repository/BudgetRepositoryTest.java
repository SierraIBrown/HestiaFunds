package com.SierraIBrown.HestiaFundsBackend.repository;

import com.SierraIBrown.HestiaFundsBackend.model.Budget;
import com.SierraIBrown.HestiaFundsBackend.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ActiveProfiles("test")
public class BudgetRepositoryTest {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    public void setUp(){
        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory.setColor("#FFFFFF");
        categoryRepository.save(testCategory);
    }

    /*
    Tests getting budget category by category id
     */
    @Test
    public void testFindByCategoryId(){
        Budget budget = new Budget();
        budget.setCategory(testCategory);
        budget.setAmount(BigDecimal.valueOf(500));
        budget.setPeriodStart(LocalDate.of(2025, 1, 1));
        budget.setPeriodEnd(LocalDate.of(2025, 1, 31));
        Budget savedBudget = budgetRepository.save(budget);

        assertThat(savedBudget.getId()).isNotNull();

        List<Budget> budgets = budgetRepository.findAll();
        assertThat(budgets).hasSize(1);
        Budget retrievedBudget = budgets.get(0);
        assertThat(retrievedBudget.getAmount()).isEqualByComparingTo("500");
        assertThat(retrievedBudget.getCategory().getName()).isEqualTo("Test Category");
        assertThat(retrievedBudget.getPeriodStart()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(retrievedBudget.getPeriodEnd()).isEqualTo(LocalDate.of(2025, 1, 31));
    }
}
