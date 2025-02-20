package com.PolicyManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    @NotBlank(message = "Name cannot be blank.")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
    private String name;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email must be a valid format.")
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role cannot be null.")
    private Role role = Role.ADMIN;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Scheme> schemes;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Policy> policies;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Claim> claims;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Customers> customers;

    public Admin() {}

    public Admin(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Scheme> getSchemes() {
        return schemes;
    }

    public void setSchemes(List<Scheme> schemes) {
        this.schemes = schemes;
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

    public List<Customers> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customers> customers) {
        this.customers = customers;
    }

    public Admin(Long adminId, String name, String email, Role role, String password, List<Scheme> schemes, List<Policy> policies, List<Claim> claims, List<Customers> customers) {
        this.adminId = adminId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
        this.schemes = schemes;
        this.policies = policies;
        this.claims = claims;
        this.customers = customers;
    }
}
