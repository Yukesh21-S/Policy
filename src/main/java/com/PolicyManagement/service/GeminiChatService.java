package com.PolicyManagement.service;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GeminiChatService {

    @Value("${gemini.api.key}")
    private String apiKey;  // Ensure your API key is stored securely

    private final WebClient webClient;

    public GeminiChatService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.gemini.com")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public Mono<String> sendMessage(String message) {
        return webClient.post()
                .uri("/chat")
                .bodyValue("{\"message\": \"" + message + "\"}")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(error -> {
                    // Log and return a fallback response
                    return Mono.just("Error: Unable to reach the Gemini API");
                });
    }
}
