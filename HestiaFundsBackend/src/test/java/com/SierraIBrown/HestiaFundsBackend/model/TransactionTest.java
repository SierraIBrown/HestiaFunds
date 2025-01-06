package com.SierraIBrown.HestiaFundsBackend.model;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class TransactionTest {

    @Test
    void testConstructorAndGetters(){
        //given
        BigDecimal amount = BigDecimal.valueOf(25.75);
        LocalDate date = LocalDate.of(2025, 1, 5);
        String description = "Xbox Game Pass Ultimate";
        Category category = new Category("Subscriptions");

        //when
        Transaction transaction = new Transaction(amount, date, description, category);

        //then
        assertThat(transaction.getAmount()).isEqualByComparingTo("25.75");
        assertThat(transaction.getDate()).isEqualTo(LocalDate.of(2025, 1, 5));
        assertThat(transaction.getDescription()).isEqualTo("Xbox Game Pass Ultimate");
        assertThat(transaction.getCategory()).isEqualTo(category);
        assertThat(transaction.getId()).isNull();
    }

    @Test
    void testSetters(){
        //given
        Transaction transaction = new Transaction();
        BigDecimal newAmount = BigDecimal.valueOf(50.00);
        LocalDate newDate = LocalDate.of(2025, 1, 10);
        String newDescription = "Movie Tickets";
        Category newCategory = new Category("Entertainment");

        //when
        transaction.setAmount(newAmount);
        transaction.setDate(newDate);
        transaction.setDescription(newDescription);
        transaction.setCategory(newCategory);

        //then
        assertThat(transaction.getAmount()).isEqualByComparingTo("50.00");
        assertThat(transaction.getDate()).isEqualTo(LocalDate.of(2025, 1, 10));
        assertThat(transaction.getDescription()).isEqualTo("Movie Tickets");
        assertThat(transaction.getCategory()).isEqualTo(newCategory);
    }
}
