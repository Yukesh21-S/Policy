package com.PolicyManagement.controller;

import com.PolicyManagement.service.GeminiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;  // Import ModelAndView
import reactor.core.publisher.Mono;

@Controller // Use Controller for rendering views
public class ChatController {

    @Autowired
    private GeminiChatService geminiChatService;

    @GetMapping("/")
    public ModelAndView homePage() {
        // Return the home page (chat.html) without any data initially
        return new ModelAndView("chat");
    }

    @PostMapping("/chat")
    public ModelAndView chat(@RequestParam String message) {
        // Send the message to Gemini AI and get the response
        Mono<String> response = geminiChatService.sendMessage(message);

        // Create a new ModelAndView object and set the view name
        ModelAndView modelAndView = new ModelAndView("chat");

        // Add the response as an attribute to the model
        modelAndView.addObject("response", response.block());  // block() to wait for the response

        // Return the ModelAndView to render the view with the model data
        return modelAndView;
    }
}
