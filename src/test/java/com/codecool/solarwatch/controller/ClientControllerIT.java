package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.payload.RegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ExtendWith() if we want to extend use this, (not RunWith)
@SpringBootTest
@AutoConfigureMockMvc //no need to run tomcat
@TestPropertySource(locations = "classpath:application-integrationtest.properties") // not necessary needed as resources have it in the test
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
        registrationRequest.setUsername("testregister");
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
        registrationRequest.setUsername("testlogin");
        registrationRequest.setPassword("asd");

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());

        ResultActions resultActions = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").exists())
                .andExpect(jsonPath("$.username").value("testlogin"))
                .andExpect(jsonPath("$.roles").isArray());
    }
}
