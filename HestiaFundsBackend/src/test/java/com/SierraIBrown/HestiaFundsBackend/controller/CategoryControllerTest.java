package com.SierraIBrown.HestiaFundsBackend.controller;

import com.SierraIBrown.HestiaFundsBackend.model.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /*
    For JSON serialization/deserialization
     */
    @Autowired
    private ObjectMapper objectMapper;

    /*
    Tests get all categories
     */
    @Test
    void testGetAllCategories() throws Exception{
        mockMvc.perform(get("/api/categories"))
                .andExpect((status().isOk()));
    }

    /*
    Tests creating new Category
     */
    @Test
    void testCreateCategory() throws Exception{
        Category newCategory = new Category();
        newCategory.setName("Pet Supplies");

        mockMvc.perform(post("/api/categories")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated());
    }

    /*
    Tests updating/editing an existing category
     */
    @Test
    void testUpdateCategory() throws Exception{
        Category category = new Category();
        category.setId(1L);
        category.setName("Updated Category Name");

        mockMvc.perform(put("/api/categories/{id}", 1L)
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
        mockMvc.perform(delete("/api/categories/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Category deleted successfully"));
    }
}
