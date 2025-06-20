package br.com.littlebee.keycloak.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("/public deve retornar 200 e a mensagem correta")
    void publicEndpointShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello from public endpoint!"));
    }

    @Test
    @DisplayName("/user deve retornar 401 sem autenticação")
    void userEndpointShouldReturn401WithoutAuth() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("/admin deve retornar 401 sem autenticação")
    void adminEndpointShouldReturn401WithoutAuth() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("/user deve retornar 200 para USER com a mensagem correta")
    void userEndpointShouldReturn200ForUser() throws Exception {
        mockMvc.perform(get("/user")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject("test-user-id"))
                                .authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello from user endpoint, test-user-id!"));
    }

    @Test
    @DisplayName("/admin deve retornar 200 para ADMIN com a mensagem correta")
    void adminEndpointShouldReturn200ForAdmin() throws Exception {
        mockMvc.perform(get("/admin")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject("test-admin-id"))
                                .authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello from admin endpoint, test-admin-id!"));
    }

    @Test
    @DisplayName("/admin deve retornar 403 para USER")
    void adminEndpointShouldReturn403ForUser() throws Exception {
        mockMvc.perform(get("/admin")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }
}
