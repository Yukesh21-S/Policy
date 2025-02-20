package com.PolicyManagement.model;



import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;


import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private Double amount;
    private String method; // Card, UPI, Bank Transfer
    private Date date;

    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customers customer;  // This establishes the relationship with the Customer entity

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public Customers getCustomer() {
        return customer;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }

    public Payment(Long paymentId, Double amount, String method, Date date, Policy policy, Customers customer) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.method = method;
        this.date = date;
        this.policy = policy;
        this.customer = customer;
    }

    public Payment() {
    }
}
