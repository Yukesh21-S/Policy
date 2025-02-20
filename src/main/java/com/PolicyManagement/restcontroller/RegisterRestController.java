package com.PolicyManagement.restcontroller;

import com.PolicyManagement.emailservice.EmailService;
import com.PolicyManagement.model.Customers;
import com.PolicyManagement.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/registration")
public class RegisterRestController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerService customerService;

    // Create a new customer
    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody Customers registrationDto) {
        try {
            Customers customer = customerService.save(registrationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Customer successfully registered!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while registering customer.");
        }
    }

    // Handle password reset: request OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<?> processForgotPassword(@RequestParam("email") String email, HttpSession session) {
        Optional<Customers> customerOptional = customerService.findByEmail(email);
        if (!customerOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not registered!");
        }

        // Generate OTP
        String otp = String.valueOf(new Random().nextInt(999999));
        session.setAttribute("otp", otp);
        session.setAttribute("email", email);

        // Send OTP via email
        String emailContent = "Your OTP for password reset is: " + otp;
        emailService.sendEmail(email, "Password Reset OTP", emailContent);

        return ResponseEntity.ok("OTP has been sent to your email.");
    }

    // Handle OTP verification
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam("otp") String userOtp, HttpSession session) {
        String sessionOtp = (String) session.getAttribute("otp");
        if (sessionOtp == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Session expired. Please request a new OTP.");
        }

        if (userOtp.equals(sessionOtp)) {
            return ResponseEntity.ok("OTP verified successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
        }
    }

    // Handle password reset
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("newPassword") String newPassword, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Session expired. Please restart the password reset process.");
        }

        Optional<Customers> customerOptional = customerService.findByEmail(email);
        if (customerOptional.isPresent()) {
            Customers customer = customerOptional.get();
            customer.setPassword(passwordEncoder.encode(newPassword));
            customerService.updateCustomer(customer);

            // Invalidate session attributes
            session.removeAttribute("otp");
            session.removeAttribute("email");

            return ResponseEntity.ok("Password reset successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found.");
        }
    }

}