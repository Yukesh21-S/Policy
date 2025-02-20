package com.PolicyManagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity

public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    private String name;
    private String description;
    private Double premiumAmount;


    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    private Double totalPremiumAmount;
    private Double maturityAmount;
    private Integer numberOfYears;

    @Enumerated(EnumType.STRING)
    private PolicyStatus policyStatus;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private AnnuityTerm annuityTerm;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "scheme_id")
    private Scheme scheme;

    @ManyToMany
    @JoinTable(
            name = "customer_policy",
            joinColumns = @JoinColumn(name = "policy_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private List<Customers> customers;


    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL)
    private List<Claim> claims;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL)
    private List<Payment> payments;
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;

    @Column(name = "image_url")
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }
    @Transient
    private boolean claimRaised;

    // Getter for claimRaised
    public boolean isClaimRaised() {
        return claimRaised;
    }

    // Setter for claimRaised
    public void setClaimRaised(boolean claimRaised) {
        this.claimRaised = claimRaised;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }



    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
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

    public Double getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(Double premiumAmount) {
        this.premiumAmount = premiumAmount;
    }



    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Double getTotalPremiumAmount() {
        return totalPremiumAmount;
    }

    public void setTotalPremiumAmount(Double totalPremiumAmount) {
        this.totalPremiumAmount = totalPremiumAmount;
    }

    public Double getMaturityAmount() {
        return maturityAmount;
    }

    public void setMaturityAmount(Double maturityAmount) {
        this.maturityAmount = maturityAmount;
    }

    public Integer getNumberOfYears() {
        return numberOfYears;
    }

    public void setNumberOfYears(Integer numberOfYears) {
        this.numberOfYears = numberOfYears;
    }

    public PolicyStatus getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(PolicyStatus policyStatus) {
        this.policyStatus = policyStatus;
    }

    public AnnuityTerm getAnnuityTerm() {
        return annuityTerm;
    }

    public void setAnnuityTerm(AnnuityTerm annuityTerm) {
        this.annuityTerm = annuityTerm;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
    }


    public List<Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }


    public Policy() {

    }
    public Policy(String name, String description, Double premiumAmount, Date startDate,
                  Double totalPremiumAmount, Double maturityAmount, Integer numberOfYears,
                  PolicyStatus policyStatus, AnnuityTerm annuityTerm) {
        this.name = name;
        this.description = description;
        this.premiumAmount = premiumAmount;
        this.startDate = startDate;
        this.totalPremiumAmount = totalPremiumAmount;
        this.maturityAmount = maturityAmount;
        this.numberOfYears = numberOfYears;
        this.policyStatus = policyStatus;
        this.annuityTerm = annuityTerm;
    }

    public Policy(Long policyId, String name, String description, Double premiumAmount, Date startDate, Double totalPremiumAmount, Double maturityAmount, Integer numberOfYears, PolicyStatus policyStatus, PaymentStatus paymentStatus, AnnuityTerm annuityTerm, Admin admin, Scheme scheme, List<Customers> customers, List<Claim> claims, List<Payment> payments) {
        this.policyId = policyId;
        this.name = name;
        this.description = description;
        this.premiumAmount = premiumAmount;
        this.startDate = startDate;
        this.totalPremiumAmount = totalPremiumAmount;
        this.maturityAmount = maturityAmount;
        this.numberOfYears = numberOfYears;
        this.policyStatus = policyStatus;
        this.paymentStatus = paymentStatus;
        this.annuityTerm = annuityTerm;
        this.admin = admin;
        this.scheme = scheme;
        this.customers = customers;
        this.claims = claims;
        this.payments = payments;
    }

    public Policy(Long policyId, String name, String description, Double premiumAmount, Date startDate, Double totalPremiumAmount, Double maturityAmount, Integer numberOfYears, PolicyStatus policyStatus, PaymentStatus paymentStatus, AnnuityTerm annuityTerm, Admin admin, Scheme scheme, List<Customers> customers, List<Claim> claims, List<Payment> payments, List<Feedback> feedbacks) {
        this.policyId = policyId;
        this.name = name;
        this.description = description;
        this.premiumAmount = premiumAmount;
        this.startDate = startDate;
        this.totalPremiumAmount = totalPremiumAmount;
        this.maturityAmount = maturityAmount;
        this.numberOfYears = numberOfYears;
        this.policyStatus = policyStatus;
        this.paymentStatus = paymentStatus;
        this.annuityTerm = annuityTerm;
        this.admin = admin;
        this.scheme = scheme;
        this.customers = customers;
        this.claims = claims;
        this.payments = payments;
        this.feedbacks = feedbacks;
    }

    public List<Customers> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customers> customers) {
        this.customers = customers;
    }

    // Getters and Setters

    public Policy(Long policyId, String name, String description, Double premiumAmount, Date startDate, Double totalPremiumAmount, Double maturityAmount, Integer numberOfYears, PolicyStatus policyStatus, PaymentStatus paymentStatus, AnnuityTerm annuityTerm, Admin admin, Scheme scheme, List<Customers> customers, List<Claim> claims, List<Payment> payments, List<Feedback> feedbacks, String imageUrl) {
        this.policyId = policyId;
        this.name = name;
        this.description = description;
        this.premiumAmount = premiumAmount;
        this.startDate = startDate;
        this.totalPremiumAmount = totalPremiumAmount;
        this.maturityAmount = maturityAmount;
        this.numberOfYears = numberOfYears;
        this.policyStatus = policyStatus;
        this.paymentStatus = paymentStatus;
        this.annuityTerm = annuityTerm;
        this.admin = admin;
        this.scheme = scheme;
        this.customers = customers;
        this.claims = claims;
        this.payments = payments;
        this.feedbacks = feedbacks;
        this.imageUrl = imageUrl;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }
}