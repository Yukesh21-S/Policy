package com.PolicyManagement.model;

import jakarta.persistence.*;

@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    private String street;
    private String city;
    private String state;
    private String country;

//    @OneToOne
//    @JoinColumn(name = "customer_id",referencedColumnName = "customerId") // Foreign key in Address table
//    private Customers customers;

    public Address() {
    }

    public Address(Long addressId, String street, String city, String state, String country) {
        this.addressId = addressId;
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        //this.customers = customers;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
//
//    public Customers getCustomers() {
//        return customers;
//    }
//
//    public void setCustomers(Customers customers) {
//        this.customers = customers;
//    }
}
