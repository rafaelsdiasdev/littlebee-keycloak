package br.com.littlebee.keycloak.controller;

import br.com.littlebee.keycloak.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DemoController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class DemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicEndpointShouldReturnOk() throws Exception {
        mockMvc.perform(get("/public"))
                .andExpect(status().isOk())
                .andExpect(content().string("Endpoint público!"));
    }

    @Test
    void healthEndpointShouldReturnOk() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void protectedEndpointsShouldReturnUnauthorizedWhenNoToken() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/admin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointsShouldReturnOkWithValidToken() throws Exception {
        mockMvc.perform(get("/user")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(content().string("Endpoint protegido para USER ou ADMIN!"));

        mockMvc.perform(get("/admin")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(content().string("Endpoint protegido para ADMIN!"));
    }
}
