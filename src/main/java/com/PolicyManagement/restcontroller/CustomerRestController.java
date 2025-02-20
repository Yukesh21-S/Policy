package com.PolicyManagement.restcontroller;

import com.PolicyManagement.emailservice.EmailService;
import com.PolicyManagement.model.*;
import com.PolicyManagement.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/api/customer")
public class CustomerRestController {

    @Autowired
    private SchemeServiceImplementation schemeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FeedBackService feedBackService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private ClaimService claimService;

    private static final Logger logger = LoggerFactory.getLogger(CustomerRestController.class);

    // Get Logged-In Customer Details
    @GetMapping("/details")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getCustomerDetails(Authentication authentication) {
        Customers customer = getAuthenticatedCustomer(authentication);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access - Customer not found");
        }
        return ResponseEntity.ok(customer);
    }

    // Update Customer Details
    @PutMapping("/update")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> updateCustomerDetails(@RequestBody Customers updatedCustomer, Authentication authentication) {
        Customers customer = getAuthenticatedCustomer(authentication);

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access - Customer not found");
        }

        try {
            customerService.update(customer.getCustomerId(), updatedCustomer);
            return ResponseEntity.ok("Customer details updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while updating customer details");
        }
    }

    // List Policies for Customer
    @GetMapping("/policies")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getCustomerPolicies(Authentication authentication) {
        Customers customer = getAuthenticatedCustomer(authentication);

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access - Customer not found");
        }

        List<Policy> policies = policyService.getPoliciesByCustomer(customer);
        return ResponseEntity.ok(policies);
    }

    // Apply for a Policy
    @PostMapping("/apply-policy/{policyId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> applyForPolicy(@PathVariable Long policyId,
                                            @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
                                            Authentication authentication) {
        Customers customer = getAuthenticatedCustomer(authentication);

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access - Customer not found");
        }

        Policy policy = policyService.getPolicyById(policyId);
        if (policy == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Policy not found");
        }

        // Check for duplicate application
        if (customer.getPolicies().stream()
                .anyMatch(existingPolicy -> existingPolicy.getPolicyId().equals(policy.getPolicyId()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have already applied for this policy");
        }

        // Process payment
        paymentMethod = paymentMethod == null ? "CARD" : paymentMethod;
        Double paymentAmount = policy.getPremiumAmount();

        Payment payment = new Payment();
        payment.setAmount(paymentAmount);
        payment.setMethod(paymentMethod);
        payment.setDate(new Date());
        payment.setPolicy(policy);

        paymentService.savePayment(payment);

        // Link the policy to the customer
        customer.getPolicies().add(policy);
        policy.getCustomers().add(customer);
        policy.setPolicyStatus(PolicyStatus.APPLIED);
        policy.setPaymentStatus(PaymentStatus.PAID);

        policyService.savePolicy(policy);

        return ResponseEntity.ok("Policy applied and payment successfully processed");
    }

    // View Active Schemes
    @GetMapping("/schemes/active")
    public ResponseEntity<?> getActiveSchemes() {
        List<Scheme> activeSchemes = schemeService.getAllActiveSchemes();
        if (activeSchemes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("No active schemes available.");
        }
        return ResponseEntity.ok(activeSchemes);
    }

    // View Scheme Details
    @GetMapping("/schemes/{id}")
    public ResponseEntity<?> getSchemeDetails(@PathVariable("id") Long id) {
        try {
            Scheme scheme = schemeService.getSchemeById(id);
            if (scheme == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Scheme not found");
            }
            return ResponseEntity.ok(scheme);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching scheme details");
        }
    }

    // Customer Payment for Policy
    @PostMapping("/payment")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> processPayment(@RequestParam("policyId") Long policyId,
                                            @RequestParam("paymentMethod") String paymentMethod,
                                            Authentication authentication) {
        Customers customer = getAuthenticatedCustomer(authentication);

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access - Customer not found");
        }

        Policy policy = policyService.getPolicyById(policyId);
        if (policy == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Policy not found");
        }

        // Process payment
        Double paymentAmount = policy.getPremiumAmount();
        Payment payment = new Payment();
        payment.setAmount(paymentAmount);
        payment.setMethod(paymentMethod);
        payment.setDate(new Date());
        payment.setPolicy(policy);
        payment.setCustomer(customer);

        paymentService.savePayment(payment);

        // Link payment and policy
        customer.getPolicies().add(policy);
        policy.getCustomers().add(customer);
        policy.setPaymentStatus(PaymentStatus.PAID);
        policyService.savePolicy(policy);

        // Send confirmation email
        String emailContent = String.format("Dear %s, your payment for policy %s has been processed.",
                customer.getName(), policy.getName());
        emailService.sendEmail(customer.getEmail(), "Payment Confirmation", emailContent);

        return ResponseEntity.ok("Payment successfully made via " + paymentMethod);
    }

    // Check Claim Status
    @GetMapping("/claim-status/{policyId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> checkClaimStatus(@PathVariable Long policyId, Authentication authentication) {
        Customers customer = getAuthenticatedCustomer(authentication);

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access - Customer not found");
        }

        Claim claim = claimService.getClaimByPolicyAndCustomer(policyId, customer.getEmail()).orElse(null);

        if (claim == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No claim found for the given policy");
        }

        return ResponseEntity.ok(claim);
    }

    // Utility method to get logged-in customer details
    private Customers getAuthenticatedCustomer(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            return customerService.findByEmail(email).orElse(null);
        }
        return null;
    }

}