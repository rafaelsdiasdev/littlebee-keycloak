package br.com.littlebee.keycloak.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class DemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("/public deve ser acessível sem autenticação")
    void publicEndpointShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/public"))
                .andExpect(status().isOk())
                .andExpect(content().string("Endpoint público!"));
    }

    @Test
    @DisplayName("/health deve ser acessível sem autenticação")
    void healthEndpointShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
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
    @DisplayName("/user deve retornar 200 para USER")
    void userEndpointShouldReturn200ForUser() throws Exception {
        mockMvc.perform(get("/user")
                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isOk())
                .andExpect(content().string("Endpoint protegido para USER ou ADMIN!"));
    }

    @Test
    @DisplayName("/admin deve retornar 200 para ADMIN")
    void adminEndpointShouldReturn200ForAdmin() throws Exception {
        mockMvc.perform(get("/admin")
                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string("Endpoint protegido para ADMIN!"));
    }

    @Test
    @DisplayName("/admin deve retornar 403 para USER")
    void adminEndpointShouldReturn403ForUser() throws Exception {
        mockMvc.perform(get("/admin")
                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(() -> "ROLE_USER")))
                .andExpect(status().isForbidden());
    }
}
