package com.SierraIBrown.HestiaFundsBackend.model;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private Long userId;

    private BigDecimal amount;

    private LocalDate periodStart;

    private LocalDate periodEnd;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDate.now();
    }

    //Getters
    public Long getId(){return id;}
    public Category getCategory(){return category;}
    public Long getUserId(){return userId;}
    public BigDecimal getAmount(){return amount;}
    public LocalDate getPeriodStart(){return periodStart;}
    public LocalDate getPeriodEnd(){return periodEnd;}
    public LocalDate getCreatedAt(){return createdAt;}
    public LocalDate getUpdatedAt(){return updatedAt;}

    //Setters
    public void setId(Long id){this.id = id;}
    public void setCategory(Category category){this.category = category;}
    public void setUserId(Long userId){this.userId = userId;}
    public void setAmount(BigDecimal amount){this.amount = amount;}
    public void setPeriodStart(LocalDate periodStart){this.periodStart = periodStart;}
    public void setPeriodEnd(LocalDate periodEnd){this.periodEnd = periodEnd;}
}
