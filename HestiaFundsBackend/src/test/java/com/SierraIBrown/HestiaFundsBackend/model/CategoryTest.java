package com.SierraIBrown.HestiaFundsBackend.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {

    /*
    Testing Category model
     */
    @Test
    void testCategoryName(){
        Category category = new Category();
        category.setName("Example");
        assertEquals("Example", category.getName());
    }
}
