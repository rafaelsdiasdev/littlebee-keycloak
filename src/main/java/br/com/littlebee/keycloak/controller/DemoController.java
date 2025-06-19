package br.com.littlebee.keycloak.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @GetMapping("/public")
    public String publicEndpoint() {
        return "Endpoint público!";
    }

    @GetMapping("/user")
    public String userEndpoint() {
        return "Endpoint protegido para USER ou ADMIN!";
    }

    @GetMapping("/admin")
    public String adminEndpoint() {
        return "Endpoint protegido para ADMIN!";
    }

    @GetMapping("/health")
    public String healthEndpoint() {
        return "OK";
    }
} 