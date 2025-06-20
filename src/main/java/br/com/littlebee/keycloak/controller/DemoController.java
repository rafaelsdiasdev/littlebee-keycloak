package br.com.littlebee.keycloak.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @GetMapping("/public")
    public ApiResponse publicEndpoint() {
        return new ApiResponse("Hello from public endpoint!");
    }

    @GetMapping("/user")
    public ApiResponse userEndpoint(@AuthenticationPrincipal Jwt jwt) {
        String message = String.format("Hello from user endpoint, %s!", jwt.getSubject());
        return new ApiResponse(message);
    }

    @GetMapping("/admin")
    public ApiResponse adminEndpoint(@AuthenticationPrincipal Jwt jwt) {
        String message = String.format("Hello from admin endpoint, %s!", jwt.getSubject());
        return new ApiResponse(message);
    }
} 