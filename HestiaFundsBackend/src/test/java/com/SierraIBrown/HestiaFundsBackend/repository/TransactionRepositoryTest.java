package com.SierraIBrown.HestiaFundsBackend.repository;

import com.SierraIBrown.HestiaFundsBackend.model.Category;
import com.SierraIBrown.HestiaFundsBackend.model.Transaction;
import org.junit.jupiter.api.DisplayName;
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
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /*
    Tests saving transaction
     */
    @Test
    @DisplayName("Should save and retrieve a Transaction with a Category")
    void testSaveAndFindTransaction(){
        //Create and save a Category
        Category cat = new Category("Subscriptions", false);
        Category savedCat = categoryRepository.save(cat);

        //Create a transaction referencing the Category
        Transaction tx = new Transaction();
        tx.setAmount(BigDecimal.valueOf(25.75));
        tx.setDate(LocalDate.of(2025, 1, 5));
        tx.setDescription("Subscription to Xbox Game Pass Ultimate");
        tx.setCategory(savedCat);

        //Save transaction
        Transaction savedTx = transactionRepository.save(tx);
        assertThat(savedTx.getId()).isNotNull();

        //Retrieve all transactions and check
        List<Transaction> allTx = transactionRepository.findAll();
        assertThat(allTx).hasSize(1);

        Transaction retrieved = allTx.get(0);
        assertThat(retrieved.getAmount()).isEqualByComparingTo("25.75");
        assertThat(retrieved.getCategory().getName()).isEqualTo("Subscriptions");
    }

    @Test
    @DisplayName("Should update a Transaction's amount")
    void testUpdateTransaction(){
        Category cat = new Category("Entertainment", false);
        Category savedCat = categoryRepository.save(cat);

        //Create transaction
        Transaction tx = new Transaction();
        tx.setAmount(BigDecimal.valueOf(50));
        tx.setDate(LocalDate.now());
        tx.setDescription("Movie Tickets");
        tx.setCategory(savedCat);
        Transaction savedTx = transactionRepository.save(tx);

        //Update transaction
        savedTx.setAmount(BigDecimal.valueOf(50.55));
        transactionRepository.save(savedTx);

        //Retrieve transaction
        Transaction updatedTx = transactionRepository.findById(savedTx.getId()).orElseThrow();
        assertThat(updatedTx.getAmount()).isEqualByComparingTo("50.55");
    }

    @Test
    @DisplayName("Should delete a Transaction")
    void testDeleteTransaction(){
        Category cat = new Category("Gas", false);
        Category savedCat = categoryRepository.save(cat);

        //Create transaction
        Transaction tx = new Transaction();
        tx.setAmount(BigDecimal.TEN);
        tx.setDate(LocalDate.now());
        tx.setCategory(savedCat);
        Transaction savedTx = transactionRepository.save(tx);

        //Delete transaction
        transactionRepository.deleteById(savedTx.getId());

        //Confirm deletion
        assertThat(transactionRepository.findById(savedTx.getId())).isEmpty();
    }
}