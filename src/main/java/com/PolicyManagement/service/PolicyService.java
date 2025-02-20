package com.PolicyManagement.service;

import com.PolicyManagement.model.Customers;
import com.PolicyManagement.model.Policy;
import com.PolicyManagement.model.PolicyStatus;
import com.PolicyManagement.repository.PolicyRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyService {
    @Autowired
    private PolicyRepo policyRepository;

    // Get all policies
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    // Save a policy
    public void savePolicy(Policy policy) {
        policyRepository.save(policy);
    }

    // Get policy by ID
    public Policy getPolicyById(Long id) {
        return policyRepository.findById(id).orElse(null);
    }

    // Delete policy by ID
    public void deletePolicy(Long id) {
        policyRepository.deleteById(id);
    }
    public void updatePolicy(Policy policy) {
        // Fetch the existing policy from the database
        Policy existingPolicy = policyRepository.findById(policy.getPolicyId())
                .orElseThrow(() -> new EntityNotFoundException("Policy with ID " + policy.getPolicyId() + " not found."));

        // Update the fields with new values
        existingPolicy.setAdmin(policy.getAdmin());
        existingPolicy.setDescription(policy.getDescription());
        existingPolicy.setName(policy.getName());
        existingPolicy.setAnnuityTerm(policy.getAnnuityTerm());
        existingPolicy.setPremiumAmount(policy.getPremiumAmount());
        existingPolicy.setStartDate(policy.getStartDate());
        existingPolicy.setTotalPremiumAmount(policy.getTotalPremiumAmount());
        existingPolicy.setMaturityAmount(policy.getMaturityAmount());
        existingPolicy.setNumberOfYears(policy.getNumberOfYears());
        existingPolicy.setPolicyStatus(policy.getPolicyStatus());
        existingPolicy.setScheme(policy.getScheme());

        // Save the updated policy
        policyRepository.save(existingPolicy);
    }
    public List<Policy> getAllAvailablePolicies() {
        // Fetch policies where status is ACTIVE or any other criteria that defines availability
        return policyRepository.findByPolicyStatus(PolicyStatus.ACTIVE); // Adjust status based on your requirements
    }


    public Optional<Policy> findPolicyById(Long policyId) {
        return policyRepository.findById(policyId);
    }

    public List<Policy> getPoliciesByCustomer(Customers customers) {
        return policyRepository.findByCustomers(customers.getCustomerId());
    }

    // Search policies by keyword
    public List<Policy> searchPolicies(String keyword) {
        return policyRepository.findByNameOrDescriptionContainingIgnoreCase(keyword);
    }


}
