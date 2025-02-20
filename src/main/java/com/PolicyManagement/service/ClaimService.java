package com.PolicyManagement.service;

import com.PolicyManagement.model.Claim;
import com.PolicyManagement.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;


    public void saveClaim(Claim claim) {
        claimRepository.save(claim);
    }


    public List<Claim> getClaimsByStatus(String status) {
        return claimRepository.findByClaimStatus(status);
    }


    public Optional<Claim> getClaimById(Long claimId) {
        return claimRepository.findById(claimId);
    }


    public List<Claim> getAllClaims() {
        return (List<Claim>) claimRepository.findAll();
    }

    public Optional<Claim> getClaimByPolicyAndCustomer(Long policyId, String customerEmail) {
        return claimRepository.findByPolicyIdAndCustomerEmail(policyId, customerEmail);
    }

}
