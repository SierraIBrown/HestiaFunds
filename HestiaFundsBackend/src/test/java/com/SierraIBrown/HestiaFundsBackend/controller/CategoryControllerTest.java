package com.SierraIBrown.HestiaFundsBackend.controller;

import com.SierraIBrown.HestiaFundsBackend.model.Category;
import com.SierraIBrown.HestiaFundsBackend.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CategoryRepository categoryRepository;

    /*
    For JSON serialization/deserialization
     */
    @Autowired
    private ObjectMapper objectMapper;

    private Long defaultCategoryId;
    private Long userCategoryId;

    @BeforeEach
    void setup(){
        categoryRepository.deleteAll();

        //Add a default category
        Category defaultCategory = new Category();
        defaultCategory.setName("Default Category");
        defaultCategory.setPreloaded(true);
        defaultCategory.setColor("#d3d3d3");
        categoryRepository.save(defaultCategory);
        defaultCategoryId = defaultCategory.getId();

        //Add a user-created category
        Category userCategory = new Category();
        userCategory.setName("User Category");
        userCategory.setPreloaded(false);
        userCategory.setColor("#f5a623");
        categoryRepository.save(userCategory);
        userCategoryId = userCategory.getId();
    }

    /*
    Tests get all categories
     */
    @Test
    void testGetAllCategories() throws Exception{
        mockMvc.perform(get("/api/categories"))
                .andExpect((status().isOk()));
    }

    /*
    Tests creating new category without a specified color
     */
    @Test
    void testCreateCategory() throws Exception{
        Category newCategory = new Category();
        newCategory.setName("Pet Supplies");

        mockMvc.perform(post("/api/categories")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Pet Supplies"))
                .andExpect(jsonPath("$.color").isNotEmpty());
    }

    /*
    Tests creating a new category with a specified color
     */
    @Test
    void testCreateCategoryWithColor() throws Exception{
        Category newCategory = new Category();
        newCategory.setName("Books");
        newCategory.setColor("#ff5733");

        mockMvc.perform(post("/api/categories")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Books"))
                .andExpect(jsonPath("$.color").value("#ff5733"));
    }

    /*
    Tests updating/editing an existing category
     */
    @Test
    void testUpdateCategory() throws Exception{
        Category category = new Category();
        category.setId(userCategoryId);
        category.setName("Updated Category Name");

        mockMvc.perform(put("/api/categories/{id}", userCategoryId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Category Name"));
    }

    /*
    Tests deleting an existing category
     */
    @Test
    void testDeleteCategory() throws Exception{
        mockMvc.perform(delete("/api/categories/{id}", userCategoryId))
                .andExpect(status().isOk())
                .andExpect(content().string("Category deleted successfully"));
    }

    /*
    Tests if it rejects editing a default category
     */
    @Test
    void testEditDefaultCategory() throws Exception{
        mockMvc.perform(put("/api/categories/{id}", defaultCategoryId)
                .contentType("application/json")
                .content("{\"name\" : \"Updated Name\"}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Cannot edit default categories."));
    }

    /*
    Tests if it rejects deleting a default category
     */
    @Test
    void testDeleteDefaultCategory() throws Exception{
        mockMvc.perform(delete("/api/categories/{id}", defaultCategoryId))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Cannot delete default categories."));
    }

    /*
    Tests if it rejects invalid color format
     */
    @Test
    void testRejectInvalidColorFormat() throws Exception{
        Category invalidCategory = new Category();
        invalidCategory.setName("Invalid");
        invalidCategory.setColor("Not a color");

        mockMvc.perform(post("/api/categories")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidCategory)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid color format."));
    }
}
