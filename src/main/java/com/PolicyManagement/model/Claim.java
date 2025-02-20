package com.PolicyManagement.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customers customer;

    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @Column(name = "claim_reason")
    private String claimReason;

    @Column(name = "claim_status")
    private String claimStatus; // E.g., PENDING, APPROVED, REJECTED

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "claim_date")
    private Date claimDate;
    @Column(name = "admin_action_message")
    private String adminActionMessage; // Admin message on the claim

    // Getters and setters for the new field
    public String getAdminActionMessage() {
        return adminActionMessage;
    }

    public void setAdminActionMessage(String adminActionMessage) {
        this.adminActionMessage = adminActionMessage;
    }
    @ManyToOne
    @JoinColumn(name = "admin_id") // Ensure the column name matches your database schema
    private Admin admin;

    // Getters and Setters

    public Claim() {}

    public Claim(Customers customer, Policy policy, String claimReason, String claimStatus, Date claimDate) {
        this.customer = customer;
        this.policy = policy;
        this.claimReason = claimReason;
        this.claimStatus = claimStatus;
        this.claimDate = claimDate;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public Customers getCustomer() {
        return customer;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public String getClaimReason() {
        return claimReason;
    }

    public void setClaimReason(String claimReason) {
        this.claimReason = claimReason;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    public Date getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(Date claimDate) {
        this.claimDate = claimDate;
    }
// Getters and Setters

    public Claim(Long claimId, Customers customer, Policy policy, String claimReason, String claimStatus, Date claimDate, String adminActionMessage, Admin admin) {
        this.claimId = claimId;
        this.customer = customer;
        this.policy = policy;
        this.claimReason = claimReason;
        this.claimStatus = claimStatus;
        this.claimDate = claimDate;
        this.adminActionMessage = adminActionMessage;
        this.admin = admin;
    }
}
