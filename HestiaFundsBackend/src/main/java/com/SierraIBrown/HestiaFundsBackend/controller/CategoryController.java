package com.SierraIBrown.HestiaFundsBackend.controller;

import com.SierraIBrown.HestiaFundsBackend.model.Category;
import com.SierraIBrown.HestiaFundsBackend.repository.CategoryRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    //GET all categories
    @GetMapping
    public ResponseEntity<?> getAllCategories(){
        List<Category> categories = categoryRepository.findAllByOrderByNameAsc();
        return ResponseEntity.ok(categories);
    }

    //POST a new category
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category categoryRequest) {
        if(categoryRequest.getName() == null || categoryRequest.getName().trim().isEmpty()){
            return ResponseEntity.badRequest().body("Category name cannot be empty");
        }
        Category saved = categoryRepository.save(categoryRequest);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    //PUT an existing category
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category categoryRequest){
        //Check if it exists
        return categoryRepository.findById(id).map(category -> {
            if(category.isPreloaded()){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot edit default categories.");
            }
            if(categoryRequest.getName() == null || categoryRequest.getName().trim().isEmpty()){
                return ResponseEntity.badRequest().body("Category name cannot be empty");
            }
            category.setName(categoryRequest.getName());
            Category updatedCategory = categoryRepository.save(category);
            return ResponseEntity.ok(updatedCategory);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found"));
    }

    //DELETE an existing category
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id){
        return categoryRepository.findById(id).map(category -> {
            if(category.isPreloaded()){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot delete default categories.");
            }
            categoryRepository.delete(category);
            return ResponseEntity.ok().body("Category deleted successfully");
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found"));
    }
}
