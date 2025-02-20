package com.PolicyManagement.controller;

import com.PolicyManagement.model.Admin;
import com.PolicyManagement.model.Scheme;
import com.PolicyManagement.repository.AdminRepo;
import com.PolicyManagement.service.SchemeServiceImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/schemes")
public class SchemeController {

    @Autowired
    private SchemeServiceImplementation schemeService;

    @Autowired
    private AdminRepo adminRepo;

    private static final Logger logger = LoggerFactory.getLogger(SchemeController.class);

    // Display the form for creating a new scheme
    @GetMapping("/new")
    public ModelAndView showCreateSchemeForm() {
        ModelAndView modelAndView = new ModelAndView("create-scheme");
        modelAndView.addObject("scheme", new Scheme());
        return modelAndView;
    }

    // Handle form submission to create/update a scheme
    @PostMapping("/save")
    public ModelAndView createScheme(@ModelAttribute Scheme scheme, RedirectAttributes redirectAttributes) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String adminEmail = user.getUsername();

        Optional<Admin> admin = adminRepo.findByEmail(adminEmail);
        scheme.setAdmin(admin.orElseThrow(() -> new RuntimeException("Admin not found")));

        if (scheme.getSchemeId() == null) {
            schemeService.createScheme(scheme);
        } else {
            schemeService.updateScheme(scheme);
        }

        logger.info("Admin with email {} created a new scheme: {}", adminEmail, scheme.getName());
        redirectAttributes.addFlashAttribute("message", "Scheme '" + scheme.getName() + "' created successfully!");
        return new ModelAndView("redirect:/admin/schemes/list-schemes");
    }

    // List all schemes
    @GetMapping("/list-schemes")
    public ModelAndView listSchemes() {
        ModelAndView modelAndView = new ModelAndView("list-schemes");
        List<Scheme> schemes = schemeService.getAllSchemes();
        modelAndView.addObject("schemes", schemes);
        return modelAndView;
    }

    // Show update form
    @GetMapping("/update/{id}")
    public ModelAndView showUpdateForm(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("create-scheme");
        Scheme scheme = schemeService.getSchemeById(id);
        modelAndView.addObject("scheme", scheme);
        return modelAndView;
    }

    // Handle deactivating a scheme
    @PostMapping("/{id}/deactivate")
    public ModelAndView deactivateScheme(@PathVariable Long id) {
        schemeService.deactivateScheme(id);
        return new ModelAndView("redirect:/admin/schemes/list-schemes");
    }

    // Handle activating a scheme
    @PostMapping("/{id}/activate")
    public ModelAndView activateScheme(@PathVariable Long id) {
        schemeService.activateScheme(id);
        return new ModelAndView("redirect:/admin/schemes/list-schemes");
    }

    // List all active schemes
    @GetMapping("/active-schemes")
    public ModelAndView listActiveSchemes() {
        ModelAndView modelAndView = new ModelAndView("list-active-schemes");
        List<Scheme> activeSchemes = schemeService.getAllActiveSchemes();
        modelAndView.addObject("activeSchemes", activeSchemes);
        return modelAndView;
    }
}
