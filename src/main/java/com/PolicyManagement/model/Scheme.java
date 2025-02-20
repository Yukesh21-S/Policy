package com.PolicyManagement.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Scheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schemeId;

    private String name;
    private String description;
    private String eligibility;
    private String benefits;
    private String status;
    private String termsAndConditions;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @OneToMany(mappedBy = "scheme", cascade = CascadeType.ALL)
    private List<Policy> policies;

    // Default constructor
    public Scheme() {
    }

    // Constructor with admin object passed directly
    public Scheme(String name, String description, String eligibility, String benefits, String status, String termsAndConditions, Admin admin) {
        this.name = name;
        this.description = description;
        this.eligibility = eligibility;
        this.benefits = benefits;
        this.status = status;
        this.termsAndConditions = termsAndConditions;
        this.admin = admin;
    }

    // Constructor with adminId and AdminRepository passed to associate Admin based on adminId


    // Constructor with all fields, including relationships
    public Scheme(Long schemeId, String name, String description, String eligibility, String benefits, String status, String termsAndConditions, Admin admin, List<Policy> policies) {
        this.schemeId = schemeId;
        this.name = name;
        this.description = description;
        this.eligibility = eligibility;
        this.benefits = benefits;
        this.status = status;
        this.termsAndConditions = termsAndConditions;
        this.admin = admin;
        this.policies = policies;
    }

    // Getters and Setters
    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEligibility() {
        return eligibility;
    }

    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
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
}
