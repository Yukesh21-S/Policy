package com.PolicyManagement.restcontroller;

import com.PolicyManagement.model.Admin;
import com.PolicyManagement.model.Customers;
import com.PolicyManagement.model.Policy;
import com.PolicyManagement.repository.AdminRepo;
import com.PolicyManagement.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/policies")
public class PolicyRestController {

    @Autowired
    private FeedBackService feedBackService;

    @Autowired
    private CustomerServiceImplementation customerService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private SchemeServiceImplementation schemeService;

    @Autowired
    private AdminRepo adminRepo;

    private static final Logger logger = LoggerFactory.getLogger(PolicyRestController.class);

    // Get all policies
    @GetMapping
    public ResponseEntity<List<Policy>> getAllPolicies() {
        try {
            List<Policy> policies = policyService.getAllPolicies();
            return ResponseEntity.ok(policies);
        } catch (Exception e) {
            logger.error("Error fetching policies", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create or update a policy
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/save", consumes = "multipart/form-data")
    public ResponseEntity<Policy> saveOrUpdatePolicy(@ModelAttribute Policy policy,
                                                     @RequestParam("image") MultipartFile image) {
        try {
            // Retrieve the authenticated user's details
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String adminEmail = user.getUsername();

            // Retrieve the Admin entity from the database
            Admin admin = adminRepo.findByEmail(adminEmail)
                    .orElseThrow(() -> new IllegalStateException("Admin not found for email: " + adminEmail));

            policy.setAdmin(admin);

            // Handle image upload if not empty
            if (!image.isEmpty()) {
                String uploadDir = "uploads" + File.separator + "policies" + File.separator;
                String imageName = UUID.randomUUID().toString() + "-" + image.getOriginalFilename();
                Path imagePath = Paths.get(uploadDir + imageName);
                Files.createDirectories(imagePath.getParent());
                Files.write(imagePath, image.getBytes());
                policy.setImageUrl("/uploads/policies/" + imageName); // Save the image URL
            }

            // Save or update the policy
            if (policy.getPolicyId() == null) {
                policyService.savePolicy(policy);
            } else {
                policyService.updatePolicy(policy);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(policy);
        } catch (IOException e) {
            logger.error("Error uploading policy image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("Error saving/updating policy", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get a specific policy by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        try {
            Policy policy = policyService.getPolicyById(id);
            if (policy == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(policy);
        } catch (Exception e) {
            logger.error("Error fetching policy by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete a policy by its ID
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        try {
            policyService.deletePolicy(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting policy", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get feedbacks for a specific policy
    @GetMapping("/{id}/feedbacks")
    public ResponseEntity<List<?>> viewPolicyFeedbacks(@PathVariable Long id) {
        try {
            var feedbacks = feedBackService.getFeedbackByPolicyId(id);
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            logger.error("Error fetching feedbacks for policy", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get customers enrolled under a specific policy
    @GetMapping("/{id}/customers")
    public ResponseEntity<?> viewCustomersForPolicy(@PathVariable Long id) {
        try {
            Policy policy = policyService.getPolicyById(id);
            if (policy == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Policy not found");
            }

            List<Customers> customers = customerService.getCustomersByPolicyId(id);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            logger.error("Error fetching customers for policy", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get claimed customers for a specific policy with claims approved
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/claimed-customers")
    public ResponseEntity<?> viewClaimedCustomersForPolicy(@PathVariable Long id) {
        try {
            Policy policy = policyService.getPolicyById(id);
            if (policy == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Policy not found");
            }

            List<Customers> allClaimedCustomers = customerService.getCustomersByClaimedPolicyId(id);

            // Filter customers with "Approved" claims
            List<Customers> approvedClaimedCustomers = allClaimedCustomers.stream()
                    .filter(customer -> customer.getClaims().stream()
                            .anyMatch(claim -> "Approved".equalsIgnoreCase(claim.getClaimStatus())
                                    && claim.getPolicy().getPolicyId().equals(id)))
                    .toList();

            if (approvedClaimedCustomers.isEmpty()) {
                return ResponseEntity.ok("No customers with approved claims for this policy.");
            }
            return ResponseEntity.ok(approvedClaimedCustomers);
        } catch (Exception e) {
            logger.error("Error fetching claimed customers for policy", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}