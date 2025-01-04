package com.SierraIBrown.HestiaFundsBackend.model;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    The amount in the transaction
     */
    private BigDecimal amount;

    /*
    Date of the transaction
     */
    private LocalDate date;

    /*
    The description of transaction
     */
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    //Constructors
    public Transaction(){}

    public Transaction(BigDecimal amount, LocalDate date, String description, Category category){
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.category = category;
    }

    //Getters
    public Long getId(){return id;}
    public BigDecimal getAmount(){return amount;}
    public LocalDate getDate(){return date;}
    public String getDescription(){return description;}
    public Category getCategory(){return category;}

    //Setters
    public void setAmount(BigDecimal amount){this.amount = amount;}
    public void setDate(LocalDate date){this.date = date;}
    public void setDescription(String description){this.description = description;}
    public void setCategory(Category category){this.category = category;}
}
