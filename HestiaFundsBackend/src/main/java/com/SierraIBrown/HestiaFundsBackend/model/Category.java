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

    private boolean preloaded;

    public Category(){}

    public Category(String name, boolean preloaded){
        this.name = name;
        this.preloaded = preloaded;
    }

    //Getters
    public Long getId(){return id;}
    public String getName(){return name;}
    public boolean isPreloaded(){return preloaded;}


    //Setters
    public void setId(Long id){this.id = id;}
    public void setName(String name){this.name = name;}
    public void setPreloaded(boolean preloaded){this.preloaded = preloaded;}
}
