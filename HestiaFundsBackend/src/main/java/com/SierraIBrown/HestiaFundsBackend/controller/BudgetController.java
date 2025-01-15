package com.SierraIBrown.HestiaFundsBackend.controller;

import com.SierraIBrown.HestiaFundsBackend.model.Budget;
import com.SierraIBrown.HestiaFundsBackend.repository.BudgetRepository;
import com.SierraIBrown.HestiaFundsBackend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public List<Budget> getAllBudgets(){
        return budgetRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id){
        return budgetRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createBudget(@RequestBody Budget budgetRequest){
        if(!categoryRepository.existsById(budgetRequest.getCategory().getId())){
            return ResponseEntity.badRequest().body("Invalid category ID.");
        }

        Budget budget = budgetRepository.save(budgetRequest);
        return ResponseEntity.ok(budget);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBudget(@PathVariable Long id, @RequestBody Budget budgetRequest){
        return budgetRepository.findById(id).map(existingBudget -> {
            if(budgetRequest.getCategory() != null && budgetRequest.getCategory().getId() != null){
                existingBudget.setCategory(budgetRequest.getCategory());
            }
            if(budgetRequest.getAmount() != null){
                existingBudget.setAmount(budgetRequest.getAmount());
            }
            if (budgetRequest.getPeriodStart() != null) {
                existingBudget.setPeriodStart(budgetRequest.getPeriodStart());
            }
            if(budgetRequest.getPeriodEnd() != null){
                existingBudget.setPeriodEnd(budgetRequest.getPeriodEnd());
            }

            Budget updatedBudget = budgetRepository.save(existingBudget);
            return ResponseEntity.ok(updatedBudget);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id){
        if(!budgetRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        budgetRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
