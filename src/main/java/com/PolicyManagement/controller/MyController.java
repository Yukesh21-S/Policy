package com.PolicyManagement.controller;

import com.PolicyManagement.model.Policy;
import com.PolicyManagement.model.Scheme;
import com.PolicyManagement.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
public class MyController {

@Autowired
private PolicyService policyService;
    @GetMapping  ("/login")
    public String login() {

        return "login";
    }

//@PostMapping("/login")
//        public String home()
//{
//    return "redirect:/customer/home";
//
//}
//    @GetMapping("/admin/login")
//    public String adminLogin()
//    {
//        return "admin-login";
//
//    }
    @GetMapping("/admin/admin-home")
    public String adminHome()
    {
        return "admin-home";
    }
    @GetMapping("/home")
    public String handleWelcome() {
        return "dummyhome";
    }



}




