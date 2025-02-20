package com.PolicyManagement.restcontroller;

import com.PolicyManagement.model.Claim;
import com.PolicyManagement.model.Customers;
import com.PolicyManagement.model.Policy;
import com.PolicyManagement.service.ClaimService;
import com.PolicyManagement.service.CustomerService;
import com.PolicyManagement.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/claims")
public class ClaimRestController {

    @Autowired
    private ClaimService claimService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private CustomerService customerService;

    // Submit a claim for a specific policy
    @PostMapping("/claimPolicy/{policyId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> claimPolicy(@PathVariable Long policyId,
                                         @RequestParam String claimReason,
                                         Authentication authentication) {
        try {
            // Fetch the authenticated customer
            Customers loggedInCustomer = getAuthenticatedCustomer(authentication);
            if (loggedInCustomer == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated!");
            }

            // Retrieve the policy and check if it exists
            Policy policy = policyService.getPolicyById(policyId);
            if (policy == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Policy not found!");
            }

            // Check if the customer has already claimed the policy
            boolean alreadyClaimed = loggedInCustomer.getClaims().stream()
                    .anyMatch(claim -> claim.getPolicy().getPolicyId().equals(policyId));
            if (alreadyClaimed) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have already claimed this policy.");
            }

            // Create and save the new claim
            Claim claim = new Claim();
            claim.setCustomer(loggedInCustomer);
            claim.setPolicy(policy);
            claim.setClaimReason(claimReason);
            claim.setClaimStatus("PROCESSING"); // Default status
            claim.setClaimDate(new Date());
            claimService.saveClaim(claim);

            return ResponseEntity.status(HttpStatus.CREATED).body("Your claim has been successfully submitted and is under review.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while submitting your claim.");
        }
    }

    // Admin view all claims
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Claim>> viewAllClaims() {
        try {
            List<Claim> claims = claimService.getAllClaims();
            return ResponseEntity.ok(claims);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Admin updates the claim status
    @PostMapping("/admin/{claimId}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateClaimStatus(@PathVariable Long claimId,
                                               @RequestParam String newStatus) {
        try {
            // Retrieve the claim
            Optional<Claim> claimOptional = claimService.getClaimById(claimId);
            if (claimOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Claim not found!");
            }

            Claim claim = claimOptional.get();

            // Update the claim status
            claim.setClaimStatus(newStatus);
            claimService.saveClaim(claim);

            return ResponseEntity.ok("Claim status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating claim status.");
        }
    }

    // Helper method to fetch the authenticated customer
    private Customers getAuthenticatedCustomer(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
            return customerService.findByEmail(email).orElse(null);
        }
        return null;
    }
}