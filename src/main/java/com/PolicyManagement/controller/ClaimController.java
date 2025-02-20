package com.PolicyManagement.controller;

import com.PolicyManagement.model.Claim;
import com.PolicyManagement.model.Customers;
import com.PolicyManagement.model.Policy;
import com.PolicyManagement.service.ClaimService;
import com.PolicyManagement.service.CustomerService;
import com.PolicyManagement.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/claim")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private CustomerService customerService;

    /**
     * Customer claims a policy with a reason.
     */
    @GetMapping("/claimPolicy/{policyId}")
    public ModelAndView showClaimPolicyPage(@PathVariable("policyId") Long policyId) {
        Policy policy = policyService.getPolicyById(policyId);
        ModelAndView modelAndView = new ModelAndView("claim-policy");
        if (policy == null) {
            modelAndView.addObject("error", "Policy not found!");
            modelAndView.setViewName("redirect:/customer/my-policies");
            return modelAndView;
        }
        modelAndView.addObject("policy", policy);
        return modelAndView; // Return the claim-policy view
    }

    // Submit the claim for the selected policy
    @PostMapping("/claimPolicy/{policyId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ModelAndView claimPolicy(@PathVariable Long policyId,
                                    @RequestParam String claimReason,
                                    Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView();

        // Fetch the authenticated customer
        Customers loggedInCustomer = getAuthenticatedCustomer(authentication);
        if (loggedInCustomer == null) {
            modelAndView.addObject("error", "User not authenticated!");
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }

        // Retrieve the policy and check if it exists
        Policy policy = policyService.getPolicyById(policyId);
        if (policy == null) {
            modelAndView.addObject("error", "Policy not found!");
            modelAndView.setViewName("redirect:/customer/my-policies");
            return modelAndView;
        }

        // Check if the customer has already claimed the policy
        boolean alreadyClaimed = loggedInCustomer.getClaims().stream()
                .anyMatch(claim -> claim.getPolicy().getPolicyId().equals(policyId));
        if (alreadyClaimed) {
            modelAndView.addObject("error", "You have already claimed this policy.");
            modelAndView.setViewName("claim-policy");
            return modelAndView;
        }

        // Create and save the new claim
        Claim claim = new Claim();
        claim.setCustomer(loggedInCustomer);
        claim.setPolicy(policy);
        claim.setClaimReason(claimReason);
        claim.setClaimStatus("PROCESSING"); // Default status
        claim.setClaimDate(new Date());
        claimService.saveClaim(claim);

        modelAndView.addObject("message", "Your claim has been successfully submitted and is under review.");
        modelAndView.setViewName("redirect:/customer/home"); // Redirect to the customer's home page
        return modelAndView;
    }

    /**
     * Admin view: List all claims for review.
     */
    @GetMapping("/admin/claims")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView viewAllClaims() {
        List<Claim> claims = claimService.getAllClaims();
        ModelAndView modelAndView = new ModelAndView("view-claims");
        modelAndView.addObject("claims", claims);
        return modelAndView; // Return admin claims view
    }

    /**
     * Admin updates the claim status.
     */
    @PostMapping("/admin/claims/{claimId}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView updateClaimStatus(@PathVariable Long claimId,
                                          @RequestParam String newStatus) {
        ModelAndView modelAndView = new ModelAndView();

        // Retrieve the claim
        Optional<Claim> claimOptional = claimService.getClaimById(claimId);
        if (claimOptional.isEmpty()) {
            modelAndView.addObject("error", "Claim not found!");
            modelAndView.setViewName("redirect:/admin/claims");
            return modelAndView;
        }

        Claim claim = claimOptional.get();

        // Update the claim status
        claim.setClaimStatus(newStatus);
        claimService.saveClaim(claim);

        // Provide success message
        modelAndView.addObject("message", "Claim status updated successfully.");
        modelAndView.setViewName("redirect:/admin/claims");
        return modelAndView;
    }

    /**
     * Helper method to fetch the authenticated customer.
     */
    private Customers getAuthenticatedCustomer(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
            return customerService.findByEmail(email).orElse(null);
        }
        return null;
    }
}