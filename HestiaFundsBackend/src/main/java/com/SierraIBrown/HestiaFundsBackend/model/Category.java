package com.SierraIBrown.HestiaFundsBackend.model;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Category(){}

    public Category(String name){this.name = name;}

    //Getters
    public Long getId(){return id;}
    public String getName(){return name;}


    //Setters
    public void setId(Long id){this.id = id;}
    public void setName(String name){this.name = name;}
}
