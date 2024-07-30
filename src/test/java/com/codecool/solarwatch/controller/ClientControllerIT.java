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

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
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

    // -------------------- Registration Tests --------------------

    @Test
    void registerUser_withValidDetails_thenReturnsOk() throws Exception {
        RegistrationRequest request = createRegistrationRequest("unique@test.com", "uniqueUser");

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void registerUser_withMissingFields_thenReturnsBadRequest() throws Exception {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstName("user");

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_withDuplicateUsername_thenReturnsConflict() throws Exception {
        RegistrationRequest request = createRegistrationRequest("uniqueEmailForDupUsername@test.com", "duplicateUsernameTest");

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        RegistrationRequest duplicateUsernameRequest = createRegistrationRequest("anotherEmailForDuplicateUsername@test.com", "duplicateUsernameTest");
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUsernameRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(containsString("Username already exists")))
                .andExpect(jsonPath("$.error").value(not(containsString("Email already exists"))));
    }

    @Test
    void registerUser_withDuplicateEmail_thenReturnsConflict() throws Exception {
        RegistrationRequest request = createRegistrationRequest("uniqueEmail@test.com", "uniqueUsername");

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        RegistrationRequest duplicateEmailRequest = createRegistrationRequest("uniqueEmail@test.com", "anotherUsername");
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEmailRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(containsString("Email already exists")))
                .andExpect(jsonPath("$.error").value(not(containsString("Username already exists"))));
    }

    @Test
    void registerUser_withDuplicateEmailAndUsername_thenReturnsConflict() throws Exception {
        RegistrationRequest request = createRegistrationRequest("duplicate@test.com", "testUser");

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(containsString("Username already exists")))
                .andExpect(jsonPath("$.error").value(containsString("Email already exists")));

    }

    // -------------------- Authentication Tests --------------------

    @Test
    void authenticateUserSuccessfully() throws Exception {
        RegistrationRequest registrationRequest = createRegistrationRequest("testlogin@test.com", "testLogin");
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
    void authenticateUser_withInvalidCredentials_thenReturnsUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistentUser");
        loginRequest.setPassword("wrongPassword");

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    // -------------------- Endpoint Access Tests --------------------

    @Test
    void accessPublicEndpoint() throws Exception {
        mvc.perform(get("/api/auth/test/public")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("This is a public endpoint!"));
    }

    @Test
    @WithMockUser(username = "userAuth", roles = "USER")
    void accessUserEndpoint_whenUserRole_thenReturnsOk() throws Exception {
        mvc.perform(get("/api/auth/test/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Secured Endpoint Accessed by user: userAuth"));
    }

    @Test
    @WithMockUser(username = "adminAuth", roles = "ADMIN")
    void accessAdminEndpoint_whenAdminRole_thenReturnsOk() throws Exception {
        mvc.perform(get("/api/auth/test/admin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin Endpoint Accessed by admin: adminAuth"));
    }

    @Test
    @WithMockUser(username = "userAuth", roles = "USER")
    void accessAdminEndpoint_whenUserRole_thenReturnsForbidden() throws Exception {
        mvc.perform(get("/api/auth/test/admin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // -------------------- Helper Methods --------------------

    private RegistrationRequest createRegistrationRequest(String email, String username) {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstName("user");
        request.setLastName("test");
        request.setDateOfBirth(LocalDate.of(2020, 5, 4));
        request.setEmail(email);
        request.setUsername(username);
        request.setPassword("asd");
        return request;
    }
}