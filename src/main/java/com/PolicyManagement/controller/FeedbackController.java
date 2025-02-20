package com.PolicyManagement.controller;

import com.PolicyManagement.model.Customers;
import com.PolicyManagement.model.Feedback;
import com.PolicyManagement.model.Policy;
import com.PolicyManagement.service.CustomerService;
import com.PolicyManagement.service.FeedBackService;
import com.PolicyManagement.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/customer/feedback")
public class FeedbackController {

    @Autowired
    private FeedBackService feedbackService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private CustomerService customerService;

    // Handle feedback submission
    @PostMapping("/submit/{policyId}")
    public ModelAndView submitFeedback(@PathVariable Long policyId,
                                       @RequestParam String comment,
                                       @RequestParam int rating,
                                       Principal principal) {
        // Get the customer based on the logged-in user's email
        String customerEmail = principal.getName();
        Customers customer = customerService.findByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // Retrieve the policy by policyId
        Policy policy = policyService.getPolicyById(policyId);

        // Create and save feedback
        Feedback feedback = new Feedback();
        feedback.setCustomer(customer);
        feedback.setPolicy(policy);
        feedback.setComment(comment);
        feedback.setRating(rating);
        feedback.setSubmittedAt(LocalDateTime.now());

        feedbackService.saveFeedback(feedback);

        // Return ModelAndView to show success page
        ModelAndView modelAndView = new ModelAndView("feedback-success");
        modelAndView.addObject("message", "Feedback submitted successfully!");
        return modelAndView;
    }

    // Display success page after submitting feedback
    @GetMapping("/success")
    public ModelAndView feedbackSuccess() {
        return new ModelAndView("feedback-success");
    }

    // View feedbacks for a specific policy (if required)
    @GetMapping("/policy/{policyId}")
    public ModelAndView viewFeedbacksForPolicy(@PathVariable Long policyId) {
        List<Feedback> feedbacks = feedbackService.getFeedbackByPolicyId(policyId);
        ModelAndView modelAndView = new ModelAndView("feedback-list");
        modelAndView.addObject("feedbacks", feedbacks);
        modelAndView.addObject("policy", policyService.getPolicyById(policyId));
        return modelAndView;
    }
}
