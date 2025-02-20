package com.PolicyManagement.restcontroller;

import com.PolicyManagement.model.Claim;
import com.PolicyManagement.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/claims")
@PreAuthorize("hasRole('ADMIN')") // Ensures all methods require the ADMIN role
public class AdminClaimRestController {

    @Autowired
    private ClaimService claimService;

    // View all pending claims
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingClaims() {
        try {
            List<Claim> pendingClaims = claimService.getClaimsByStatus("PROCESSING");
            if (pendingClaims.isEmpty()) {
                return ResponseEntity.ok("No pending claims at the moment.");
            }
            return ResponseEntity.ok(pendingClaims);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching pending claims.");
        }
    }

    // Update claim status
    @PostMapping("/updateStatus/{claimId}")
    public ResponseEntity<?> updateClaimStatus(
            @PathVariable @NotNull Long claimId,
            @RequestParam @NotNull String status) {

        try {
            // Validate claim existence
            Optional<Claim> optionalClaim = claimService.getClaimById(claimId);
            if (optionalClaim.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Claim not found!");
            }

            Claim claim = optionalClaim.get();

            // Validate the status
            if (!isValidStatus(status)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid status! Valid statuses are: APPROVED, REJECTED, UNDER_PROCESSING.");
            }

            // Update the claim status and admin action message
            claim.setClaimStatus(status.toUpperCase());
            claim.setAdminActionMessage(getAdminActionMessage(status));

            // Save the updated claim
            claimService.saveClaim(claim);

            // Return success response
            return ResponseEntity.ok("Claim status updated to: " + status.toUpperCase());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating claim status.");
        }
    }

    // Helper method to validate the status
    private boolean isValidStatus(String status) {
        return status.equalsIgnoreCase("APPROVED") ||
                status.equalsIgnoreCase("REJECTED") ||
                status.equalsIgnoreCase("UNDER_PROCESSING");
    }

    // Helper method to determine admin action message
    private String getAdminActionMessage(String status) {
        return switch (status.toUpperCase()) {
            case "APPROVED" -> "Your claim has been approved.";
            case "REJECTED" -> "Your claim has been rejected. Please contact support for further assistance.";
            case "UNDER_PROCESSING" -> "Your claim is under processing. Please wait for further updates.";
            default -> throw new IllegalArgumentException("Invalid status: " + status);
        };
    }
}