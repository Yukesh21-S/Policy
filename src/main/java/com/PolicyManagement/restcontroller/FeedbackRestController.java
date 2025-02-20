package com.PolicyManagement.restcontroller;

import com.PolicyManagement.model.Customers;
import com.PolicyManagement.model.Feedback;
import com.PolicyManagement.model.Policy;
import com.PolicyManagement.service.CustomerService;
import com.PolicyManagement.service.FeedBackService;
import com.PolicyManagement.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer/feedback")
public class FeedbackRestController {

    @Autowired
    private FeedBackService feedbackService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private CustomerService customerService;

    // Submit feedback for a specific policy
    @PostMapping("/submit/{policyId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> submitFeedback(@PathVariable Long policyId,
                                            @RequestParam String comment,
                                            @RequestParam int rating,
                                            Principal principal) {
        try {
            // Authenticate customer using Principal
            String customerEmail = principal.getName();
            Customers customer = customerService.findByEmail(customerEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

            // Retrieve the policy by policyId
            Policy policy = policyService.getPolicyById(policyId);
            if (policy == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Policy not found");
            }

            // Create and save feedback
            Feedback feedback = new Feedback();
            feedback.setCustomer(customer);
            feedback.setPolicy(policy);
            feedback.setComment(comment);
            feedback.setRating(rating);
            feedback.setSubmittedAt(LocalDateTime.now());

            feedbackService.saveFeedback(feedback);

            return ResponseEntity.status(HttpStatus.CREATED).body("Feedback submitted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while submitting feedback.");
        }
    }

    // View all feedbacks for a specific policy
    @GetMapping("/policy/{policyId}")
    public ResponseEntity<?> viewFeedbacksForPolicy(@PathVariable Long policyId) {
        try {
            List<Feedback> feedbacks = feedbackService.getFeedbackByPolicyId(policyId);

            if (feedbacks.isEmpty()) {
                return ResponseEntity.ok("No feedback available for this policy.");
            }

            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while fetching feedbacks.");
        }
    }
}