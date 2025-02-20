package com.PolicyManagement.restcontroller;

import com.PolicyManagement.controller.SchemeController;
import com.PolicyManagement.model.Admin;
import com.PolicyManagement.model.Scheme;
import com.PolicyManagement.repository.AdminRepo;
import com.PolicyManagement.service.SchemeServiceImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/schemes")
public class SchemeRestController {

    @Autowired
    private SchemeServiceImplementation schemeService;

    @Autowired
    private AdminRepo adminRepo;

    private static final Logger logger = LoggerFactory.getLogger(SchemeRestController.class);

    // Create or update a scheme
    @PostMapping("/save")
    public ResponseEntity<Scheme> createOrUpdateScheme(@RequestBody Scheme scheme) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String adminEmail = user.getUsername();

            // Find the admin by email
            Optional<Admin> adminOpt = adminRepo.findByEmail(adminEmail);
            if (adminOpt.isEmpty()) {
                logger.warn("Admin with email {} not found.", adminEmail);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Admin admin = adminOpt.get();
            scheme.setAdmin(admin);

            if (scheme.getSchemeId() == null) {
                schemeService.createScheme(scheme); // Save new scheme if schemeId is null
                logger.info("Admin with email {} created a new scheme: {}", adminEmail, scheme.getName());
            } else {
                schemeService.updateScheme(scheme); // Update existing scheme if schemeId exists
                logger.info("Admin with email {} updated the scheme: {}", adminEmail, scheme.getName());
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(scheme);
        } catch (Exception ex) {
            logger.error("Error creating or updating scheme: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all schemes
    @GetMapping("/list")
    public ResponseEntity<List<Scheme>> getAllSchemes() {
        logger.info("Incoming request: /api/admin/schemes/list");

        try {
            List<Scheme> schemes = schemeService.getAllSchemes();
            if (schemes == null || schemes.isEmpty()) {
                logger.warn("No schemes retrieved from the database.");
                return ResponseEntity.ok(List.of()); // Return usable empty list
            }
            return ResponseEntity.ok(schemes);
        } catch (Exception ex) {
            logger.error("Error fetching schemes: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all active schemes
    @GetMapping("/active")
    public ResponseEntity<List<Scheme>> listActiveSchemes() {
        try {
            List<Scheme> activeSchemes = schemeService.getAllActiveSchemes();
            return ResponseEntity.ok(activeSchemes);
        } catch (Exception ex) {
            logger.error("Error fetching active schemes: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get a single scheme by ID
    @GetMapping("/{id}")
    public ResponseEntity<Scheme> getSchemeById(@PathVariable Long id) {
        try {
            Scheme scheme = schemeService.getSchemeById(id);
            if (scheme == null) {
                logger.warn("Scheme with ID {} not found.", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(scheme);
        } catch (Exception ex) {
            logger.error("Error fetching scheme with ID {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update a scheme
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Scheme> updateScheme(@PathVariable("id") Long id, @RequestBody Scheme scheme) {
        try {
            scheme.setSchemeId(id);
            Scheme updatedScheme = schemeService.updateScheme(scheme);
            if (updatedScheme == null) {
                logger.warn("Failed to update scheme with ID {}.", id);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return ResponseEntity.ok(updatedScheme);
        } catch (Exception ex) {
            logger.error("Error updating scheme with ID {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Deactivate a scheme
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateScheme(@PathVariable Long id) {
        try {
            schemeService.deactivateScheme(id);
            logger.info("Scheme with ID {} has been deactivated.", id);
            return ResponseEntity.ok("Scheme with ID " + id + " has been deactivated.");
        } catch (Exception ex) {
            logger.error("Error deactivating scheme with ID {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    // Activate a scheme
    @PostMapping("/{id}/activate")
    public ResponseEntity<String> activateScheme(@PathVariable Long id) {
        try {
            schemeService.activateScheme(id);
            logger.info("Scheme with ID {} has been activated.", id);
            return ResponseEntity.ok("Scheme with ID " + id + " has been activated.");
        } catch (Exception ex) {
            logger.error("Error activating scheme with ID {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}