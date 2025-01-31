package com.SierraIBrown.HestiaFundsBackend.repository;

import com.SierraIBrown.HestiaFundsBackend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    @Query("SELECT t FROM Transaction t WHERE MONTH(t.date) = :month AND YEAR(t.date) = :year")
    List<Transaction> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

}
