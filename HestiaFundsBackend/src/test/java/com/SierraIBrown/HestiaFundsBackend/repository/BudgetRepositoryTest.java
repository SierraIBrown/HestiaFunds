package com.SierraIBrown.HestiaFundsBackend.repository;

import com.SierraIBrown.HestiaFundsBackend.model.Budget;
import com.SierraIBrown.HestiaFundsBackend.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
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

        Budget budget = new Budget();
        budget.setCategory(testCategory);
        budget.setAmount(new BigDecimal("500"));
        budget.setPeriodStart(LocalDate.of(2025, 1, 1));
        budget.setPeriodEnd(LocalDate.of(2025, 1, 31));
        budgetRepository.save(budget);
    }

    /*
    Tests getting budget category by category id
     */
    @Test
    public void testFindByCategoryId(){
        List<Budget> budgets = budgetRepository.findByCategoryId(testCategory.getId());
        assertFalse(budgets.isEmpty());
        assertEquals(1, budgets.size());
        assertEquals(testCategory.getId(), budgets.get(0).getCategory().getId());
    }

    /*
    Tests finding budget using user id and periods
     */
//    @Test
//    public void testFindByUserIdAndPeriod(){
//        List<Budget> budgets = budgetRepository.findByUserIdAndPeriodStartBeforeAndPeriodEndAfter(
//                1L, LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 15)
//        );
//        assertTrue(budgets.isEmpty());
//    }
}
