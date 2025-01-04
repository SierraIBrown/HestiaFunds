package com.SierraIBrown.HestiaFundsBackend.controller;

import com.SierraIBrown.HestiaFundsBackend.model.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
