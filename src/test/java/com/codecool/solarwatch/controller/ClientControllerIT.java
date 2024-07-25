package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.payload.LoginRequest;
import com.codecool.solarwatch.model.payload.RegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class ClientControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setFirstName("user");
        registrationRequest.setLastName("test");
        registrationRequest.setDateOfBirth(LocalDate.of(2020, 5, 4));
        registrationRequest.setEmail("testregister@test.com");
        registrationRequest.setUsername("testRegister");
        registrationRequest.setPassword("asd");

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void authenticateUser() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setFirstName("user");
        registrationRequest.setLastName("test");
        registrationRequest.setDateOfBirth(LocalDate.of(2020, 5, 4));
        registrationRequest.setEmail("testlogin@test.com");
        registrationRequest.setUsername("testLogin");
        registrationRequest.setPassword("asd");

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testLogin");
        loginRequest.setPassword("asd");

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").exists())
                .andExpect(jsonPath("$.username").value("testLogin"))
                .andExpect(jsonPath("$.roles").isArray());
    }

    @Test
    void testPublicEndpoint() throws Exception {
        // Act
        ResultActions result = mvc.perform(get("/api/auth/test/public")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(content().string("This is a public endpoint!"));
    }

    @Test
    @WithMockUser(username = "userAuth", roles = "USER")
    void testUserEndpoint() throws Exception {
        // Act
        ResultActions result = mvc.perform(get("/api/auth/test/user")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(content().string("Secured Endpoint Accessed by user: userAuth"));
    }

    @Test
    @WithMockUser(username = "adminAuth", roles = "ADMIN")
    void testAdminEndpoint() throws Exception {
        // Act
        ResultActions result = mvc.perform(get("/api/auth/test/admin")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(content().string("Admin Endpoint Accessed by admin: adminAuth"));
    }
}