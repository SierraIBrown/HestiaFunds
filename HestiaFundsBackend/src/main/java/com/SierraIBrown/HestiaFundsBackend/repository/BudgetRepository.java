package com.SierraIBrown.HestiaFundsBackend.repository;

import com.SierraIBrown.HestiaFundsBackend.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long>{
    List<Budget> findByCategoryId(Long categoryId);
    List<Budget> findByUserIdAndPeriodStartBeforeAndPeriodEndAfter(Long userId, LocalDate startDate, LocalDate endDate);
}
