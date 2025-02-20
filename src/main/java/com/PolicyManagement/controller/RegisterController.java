package com.PolicyManagement.controller;

import com.PolicyManagement.emailservice.EmailService;
import com.PolicyManagement.model.Customers;
import com.PolicyManagement.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/registration")
public class RegisterController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ModelAndView showRegistrationForm() {
        ModelAndView modelAndView = new ModelAndView("registration");
        modelAndView.addObject("customer", new Customers());
        return modelAndView;
    }

    @PostMapping
    public ModelAndView createCustomer(@ModelAttribute Customers registrationDto) {
        customerService.save(registrationDto);
        return new ModelAndView("redirect:/registration?success");
    }

    @GetMapping("/forgot-password")
    public ModelAndView showForgotPasswordForm() {
        return new ModelAndView("forgot-password");
    }

    @PostMapping("/forgot-password")
    public ModelAndView processForgotPassword(@RequestParam("email") String email, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("forgot-password");
        Optional<Customers> customerOptional = customerService.findByEmail(email);
        if (!customerOptional.isPresent()) {
            modelAndView.addObject("error", "Email not registered!");
            return modelAndView;
        }

        String otp = String.valueOf(new Random().nextInt(999999));
        session.setAttribute("otp", otp);
        session.setAttribute("email", email);

        emailService.sendEmail(email, "Password Reset OTP", "Your OTP for password reset is: " + otp);
        modelAndView.setViewName("verify-otp");
        modelAndView.addObject("message", "OTP has been sent to your email.");
        return modelAndView;
    }

    @GetMapping("/verify-otp")
    public ModelAndView showOtpForm() {
        return new ModelAndView("verify-otp");
    }

    @PostMapping("/verify-otp")
    public ModelAndView verifyOtp(@RequestParam("otp") String userOtp,
                                  @SessionAttribute("otp") String sessionOtp,
                                  ModelAndView modelAndView) {
        if (userOtp.equals(sessionOtp)) {
            modelAndView.setViewName("reset-password");
            modelAndView.addObject("message", "OTP verified successfully.");
        } else {
            modelAndView.setViewName("verify-otp");
            modelAndView.addObject("error", "Invalid OTP.");
        }
        return modelAndView;
    }

    @GetMapping("/reset-password")
    public ModelAndView showResetPasswordForm(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("reset-password");
        if (session.getAttribute("email") == null) {
            modelAndView.setViewName("forgot-password");
            modelAndView.addObject("error", "Session expired. Please start the password reset process again.");
        }
        return modelAndView;
    }

    @PostMapping("/reset-password")
    public ModelAndView resetPassword(@RequestParam("newPassword") String newPassword,
                                      @SessionAttribute("email") String email,
                                      HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("login");
        Optional<Customers> customerOptional = customerService.findByEmail(email);
        if (customerOptional.isPresent()) {
            Customers customer = customerOptional.get();
            customer.setPassword(passwordEncoder.encode(newPassword));
            customerService.updateCustomer(customer);
            session.removeAttribute("otp");
            session.removeAttribute("email");
            modelAndView.addObject("message", "Password reset successfully.");
        } else {
            modelAndView.setViewName("reset-password");
            modelAndView.addObject("error", "Customer not found.");
        }
        return modelAndView;
    }
}