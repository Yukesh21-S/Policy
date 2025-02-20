package com.PolicyManagement.model;


import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "Customers")
public class Customers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(name = "name")
    private String name;
    @Column(name="email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "status")
    private String status;
    @Column(name = "date_of_registration")
    private String dateOfRegistration;
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.CUSTOMER; // Default to CUSTOMER
    private String adminMessage;
    @ManyToMany(mappedBy = "customers")
    private List<Policy> policies;


    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Claim> claims;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;



    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "addressId")
    private Address address;
    public Customers(String name, String email, String password, String phoneNumber, String status, String dateOfRegistration, Address address,Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.dateOfRegistration = dateOfRegistration;
        this.address = address;
        this.role = role;
    }

    public Customers() {

    }


    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(String dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Customers(Long customerId, String name, String email, String password, String phoneNumber, String status, String dateOfRegistration, Admin admin, Role role, List<Policy> policies, List<Claim> claims, List<Feedback> feedbacks, Address address) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.dateOfRegistration = dateOfRegistration;
        this.admin = admin;
        this.role = role;
        this.policies = policies;
        this.claims = claims;
        this.feedbacks = feedbacks;
        this.address = address;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public List<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<Policy> policies) {
        this.policies = policies;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public String getAdminMessage() {
        return adminMessage;
    }

    public void setAdminMessage(String adminMessage) {
        this.adminMessage = adminMessage;
    }

    public Customers(Long customerId, String name, String email, String password, String phoneNumber, String status, String dateOfRegistration, Admin admin, Role role, String adminMessage, List<Policy> policies, List<Claim> claims, List<Feedback> feedbacks, Address address) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.dateOfRegistration = dateOfRegistration;
        this.admin = admin;
        this.role = role;
        this.adminMessage = adminMessage;
        this.policies = policies;
        this.claims = claims;
        this.feedbacks = feedbacks;
        this.address = address;
    }
}
