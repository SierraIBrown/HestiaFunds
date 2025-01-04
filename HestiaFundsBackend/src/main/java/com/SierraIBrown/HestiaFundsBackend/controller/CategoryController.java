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
}
