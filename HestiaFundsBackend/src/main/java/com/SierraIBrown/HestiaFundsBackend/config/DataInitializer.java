package com.SierraIBrown.HestiaFundsBackend.config;

import com.SierraIBrown.HestiaFundsBackend.model.Category;
import com.SierraIBrown.HestiaFundsBackend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer {

    @Autowired
    private CategoryRepository categoryRepository;

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event){
        long categoryCount = categoryRepository.count();
        if(categoryCount == 0){
            List<Category> defaultCategories = Arrays.asList(
                    createCategoryWithColor("Car Loans", true, "#6f4f28"),
                    createCategoryWithColor("Credit Card Bills", true, "#1d334a"),
                    createCategoryWithColor("Gas", true, "#a2231d"),
                    createCategoryWithColor("Groceries", true, "#2f353b"),
                    createCategoryWithColor("Entertainment",  true, "#015d52"),
                    createCategoryWithColor("Personal", true, "#e4a010"),
                    createCategoryWithColor("Rent/Mortgage", true, "#592321"),
                    createCategoryWithColor("Student Loans", true, "#f5d033"),
                    createCategoryWithColor("Subscriptions", true, "#3b83bd"),
                    createCategoryWithColor("Utilities", true, "#587246")
            );

            categoryRepository.saveAll(defaultCategories);
            System.out.println("Default categories have been inserted");
        }
    }

    private Category createCategoryWithColor(String name, boolean preloaded, String color){
        Category category = new Category();
        category.setName(name);
        category.setPreloaded(preloaded);
        category.setColor(color);
        return category;
    }
}
