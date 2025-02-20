package com.PolicyManagement.controller;

import com.PolicyManagement.model.Admin;
import com.PolicyManagement.model.Customers;
import com.PolicyManagement.model.Policy;
import com.PolicyManagement.repository.AdminRepo;
import com.PolicyManagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/admin/policies")
public class PolicyController {

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

    @GetMapping
    public ModelAndView getAllPolicies() {
        ModelAndView mav = new ModelAndView("policy-list");
        List<Policy> policies = policyService.getAllPolicies();
        mav.addObject("policies", policies);
        return mav;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public ModelAndView showCreateForm() {
        ModelAndView mav = new ModelAndView("policy-form");
        mav.addObject("policy", new Policy());
        mav.addObject("schemes", schemeService.getAllActiveSchemes());
        return mav;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/save", consumes = "multipart/form-data")
    public ModelAndView saveOrUpdatePolicy(@ModelAttribute("policy") Policy policy,
                                           @RequestParam("image") MultipartFile image) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String adminEmail = user.getUsername();
        Admin admin = adminRepo.findByEmail(adminEmail)
                .orElseThrow(() -> new IllegalStateException("Admin not found for email: " + adminEmail));
        policy.setAdmin(admin);

        if (!image.isEmpty()) {
            try {
                String uploadDir = "uploads" + File.separator + "policies" + File.separator;
                String imageName = UUID.randomUUID().toString() + "-" + image.getOriginalFilename();
                Path imagePath = Paths.get(uploadDir + imageName);
                Files.createDirectories(imagePath.getParent());
                Files.write(imagePath, image.getBytes());
                policy.setImageUrl("/uploads/policies/" + imageName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store the image. Error: " + e.getMessage(), e);
            }
        }

        if (policy.getPolicyId() == null) {
            policyService.savePolicy(policy);
        } else {
            policyService.updatePolicy(policy);
        }

        return new ModelAndView("redirect:/admin/policies");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public ModelAndView showUpdateForm(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView("policy-form");
        mav.addObject("policy", policyService.getPolicyById(id));
        mav.addObject("schemes", schemeService.getAllSchemes());
        return mav;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public ModelAndView deletePolicy(@PathVariable("id") Long id) {
        policyService.deletePolicy(id);
        return new ModelAndView("redirect:/admin/policies");
    }

    @GetMapping("/{id}/feedbacks")
    public ModelAndView viewPolicyFeedbacks(@PathVariable("id") Long policyId) {
        ModelAndView mav = new ModelAndView("feedback-list");
        mav.addObject("feedbacks", feedBackService.getFeedbackByPolicyId(policyId));
        mav.addObject("policyId", policyService.getPolicyById(policyId));
        return mav;
    }

    @GetMapping("/{id}/customers")
    public ModelAndView viewCustomersForPolicy(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("policy-customer");
        mav.addObject("policyName", policyService.getPolicyById(id).getName());
        mav.addObject("customers", customerService.getCustomersByPolicyId(id));
        return mav;
    }

    @GetMapping("/{id}/claimed-customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView viewClaimedCustomersForPolicy(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("view-claims");
        Policy policy = policyService.getPolicyById(id);
        if (policy == null) {
            mav.addObject("errorMessage", "Policy not found!");
            return new ModelAndView("redirect:/admin/policies");
        }

        List<Customers> allClaimedCustomers = customerService.getCustomersByClaimedPolicyId(id);
        List<Customers> approvedClaimedCustomers = allClaimedCustomers.stream()
                .filter(customer -> customer.getClaims().stream()
                        .anyMatch(claim -> "Approved".equalsIgnoreCase(claim.getClaimStatus())
                                && claim.getPolicy().getPolicyId().equals(id)))
                .toList();

        if (approvedClaimedCustomers.isEmpty()) {
            mav.addObject("message", "No customers with approved claims for this policy.");
        } else {
            mav.addObject("customers", approvedClaimedCustomers);
        }

        mav.addObject("policyName", policy.getName());
        return mav;
    }
}