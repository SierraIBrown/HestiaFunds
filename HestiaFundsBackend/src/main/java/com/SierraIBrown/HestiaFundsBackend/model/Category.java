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

    /*
    Category for CC Bills
     */
    private String ccBills;

    /*
    Category for Util Bills
     */
    private String utilBills;

    /*
    Category for Entertainment
     */
    private String entertainment;

    /*
    Category for Gas
     */
    private String gas;

    /*
    Category for Groceries
     */
    private String groceries;

    /*
    Category for Personal
     */
    private String personal;

    /*
    Category for Rent/Mortgage
     */
    private String rentMort;

    /*
    Category for Student Loans
     */
    private String studentLoans;

    /*
    Category for Car Loans
     */
    private String carLoans;

    public Category(){}

    //Getters
    public Long getId(){return id;}
    public String getCCBills(){return ccBills;}
    public String getUtilBills(){return utilBills;}
    public String getEntertainment(){return entertainment;}
    public String getGas(){return gas;}
    public String getGroceries(){return groceries;}
    public String getPersonal(){return personal;}
    public String getRentMort(){return rentMort;}
    public String getStudentLoans(){return studentLoans;}
    public String getCarLoans(){return carLoans;}

    //Setters
    public void setId(Long id){this.id = id;}
    public void setCCBills(String ccBills){this.ccBills = ccBills;}
    public void setUtilBills(String utilBills){this.utilBills = utilBills;}
    public void setEntertainment(String entertainment){this.entertainment = entertainment;}
    public void setGas(String gas){this.gas = gas;}
    public void setGroceries(String groceries){this.groceries = groceries;}
    public void setPersonal(String personal){this.personal = personal;}
    public void setRentMort(String rentMort){this.rentMort = rentMort;}
    public void setStudentLoans(String studentLoans){this.studentLoans = studentLoans;}
    public void setCarLoans(String carLoans){this.carLoans = carLoans;}
}
