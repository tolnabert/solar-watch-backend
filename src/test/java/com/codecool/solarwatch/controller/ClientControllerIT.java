package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.payload.LoginRequest;
import com.codecool.solarwatch.model.payload.RegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        mvc.perform(get("/api/auth/test/public")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("This is a public endpoint!"));
    }

    @Test
    void testAdminEndpoint() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("adminAuth");
        loginRequest.setPassword("asd");

        MvcResult loginResponse = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").exists())
                .andExpect(jsonPath("$.username").value("adminAuth"))
                .andExpect(jsonPath("$.roles").isArray())
                .andReturn();

//        String responseBody = loginResponse.getResponse().getContentAsString();
//        String jwtToken = responseBody.split("\"jwt\":\"")[1].split("\"")[0];
//
//        mvc.perform(get("/api/auth/test/admin")
//                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").value("Hello admin: adminAuth"));
    }
}