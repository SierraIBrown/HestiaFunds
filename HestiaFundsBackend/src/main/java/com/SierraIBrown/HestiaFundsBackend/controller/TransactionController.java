package com.SierraIBrown.HestiaFundsBackend.controller;

import com.SierraIBrown.HestiaFundsBackend.model.Category;
import com.SierraIBrown.HestiaFundsBackend.model.Transaction;
import com.SierraIBrown.HestiaFundsBackend.repository.CategoryRepository;
import com.SierraIBrown.HestiaFundsBackend.repository.TransactionRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /*
    Get all transactions
     */
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(){
        List<Transaction> allTx = transactionRepository.findAll();
        return ResponseEntity.ok(allTx);
    }

    /*
    Get a transaction by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id){
        return transactionRepository.findById(id)
                .map(tx -> ResponseEntity.ok().body(tx))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
    Create a new transaction
     */
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Transaction txRequest){
        //Validations
        if(txRequest.getAmount() == null){
            return ResponseEntity.badRequest().body("Amount cannot be null.");
        }
        if(txRequest.getDate() == null){
            return ResponseEntity.badRequest().body("Date cannot be null");
        }
        if(txRequest.getCategory() == null){
            return ResponseEntity.badRequest().body("Must reference a category");
        }

        //Check if the referenced Category actually exists
        Category category = categoryRepository.findById(txRequest.getCategory().getId())
                .orElse(null);
        if(category == null){
            return ResponseEntity.badRequest().body("Invalid Category: " + txRequest.getCategory().getId());
        }

        //Assign actual Category entity
        txRequest.setCategory(category);

        //Save transaction
        Transaction saved = transactionRepository.save(txRequest);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /*
    Update an existing transaction
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long id, @RequestBody Transaction txRequest){
        //Check if transaction exists
        return transactionRepository.findById(id).map(existingTx -> {
            //Update fields
            if (txRequest.getAmount() != null) {
                existingTx.setAmount(txRequest.getAmount());
            }
            if (txRequest.getDate() != null) {
                existingTx.setDate(txRequest.getDate());
            }
            if (txRequest.getDescription() != null) {
                existingTx.setDescription(txRequest.getDescription());
            }
            if (txRequest.getCategory() != null && txRequest.getCategory().getId() != null) {
                Category category = categoryRepository.findById(txRequest.getCategory().getId()).orElse(null);
                if (category == null) {
                    return ResponseEntity.badRequest().body("Invalid Category: " + txRequest.getCategory().getId());
                }
                existingTx.setCategory(category);
            }

            //Save the updated transaction
            Transaction updated = transactionRepository.save(existingTx);
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
    Delete a transaction
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id){
        return transactionRepository.findById(id).map(tx -> {
            transactionRepository.delete(tx);
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
