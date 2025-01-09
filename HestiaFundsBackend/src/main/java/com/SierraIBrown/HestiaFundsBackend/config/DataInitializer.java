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
                    new Category("Car Loans", true),
                    new Category("Credit Card Bills", true),
                    new Category("Gas", true),
                    new Category("Groceries", true),
                    new Category("Entertainment", true),
                    new Category("Personal", true),
                    new Category("Rent/Mortgage", true),
                    new Category("Student Loans", true),
                    new Category("Subscriptions", true),
                    new Category("Utilties", true)
            );

            categoryRepository.saveAll(defaultCategories);
            System.out.println("Default categories have been inserted");
        }
    }
}
