package com.PolicyManagement.controller;

import com.PolicyManagement.model.Claim;
import com.PolicyManagement.model.Customers;
import com.PolicyManagement.repository.CustomerRepository;
import com.PolicyManagement.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.constraints.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/claims")
@PreAuthorize("hasRole('ADMIN')")
public class AdminClaimController {

    @Autowired
    private ClaimService claimService;
@Autowired
private CustomerRepository customerRepository;
    @GetMapping("/pending")
    public ModelAndView getPendingClaimsPage() {
        ModelAndView modelAndView = new ModelAndView("pending-claims");
        List<Claim> pendingClaims = claimService.getClaimsByStatus("PROCESSING");
        if (pendingClaims == null || pendingClaims.isEmpty()) {
            modelAndView.addObject("info", "No pending claims at the moment.");
        } else {
            modelAndView.addObject("pendingClaims", pendingClaims);
        }
        return modelAndView;
    }

    @PostMapping("/updateStatus/{claimId}")
    public ModelAndView updateClaimStatus(
            @PathVariable @NotNull Long claimId,
            @RequestParam @NotNull String status) {

        ModelAndView modelAndView = new ModelAndView("redirect:/admin/claims/pending");

        Optional<Claim> optionalClaim = claimService.getClaimById(claimId);
        if (!optionalClaim.isPresent()) {
            modelAndView.addObject("error", "Claim not found!");
            return modelAndView;
        }

        Claim claim = optionalClaim.get();

        if (!isValidStatus(status)) {
            modelAndView.addObject("error", "Invalid status! Valid statuses are: APPROVED, REJECTED, UNDER_PROCESSING.");
            return modelAndView;
        }

        claim.setClaimStatus(status.toUpperCase());
        claim.setAdminActionMessage(getAdminActionMessage(status));
        claimService.saveClaim(claim);

        modelAndView.addObject("success", "Claim status updated to: " + status.toUpperCase());
        return modelAndView;
    }

    private boolean isValidStatus(String status) {
        return status.equalsIgnoreCase("APPROVED") ||
                status.equalsIgnoreCase("REJECTED") ||
                status.equalsIgnoreCase("UNDER_PROCESSING");
    }

    private String getAdminActionMessage(String status) {
        return switch (status.toUpperCase()) {
            case "APPROVED" -> "Your claim has been approved.";
            case "REJECTED" -> "Your claim has been rejected. Please contact support for further assistance.";
            case "UNDER_PROCESSING" -> "Your claim is under processing. Please wait for further updates.";
            default -> throw new IllegalArgumentException("Invalid status: " + status);
        };
    }
    @PostMapping("/addMessage")
    public String addAdminMessage(@RequestParam Long customerId, @RequestParam String adminMessage, RedirectAttributes redirectAttributes) {
        Optional<Customers> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isPresent()) {
            Customers customer = optionalCustomer.get();
            customer.setAdminMessage(adminMessage);
            customerRepository.save(customer);
           redirectAttributes.addFlashAttribute("message", "Admin message added successfully.");
        } else {
            redirectAttributes.addFlashAttribute("message", "Customer not found.");
        }
        return "redirect:/admin/claims";
    }

}