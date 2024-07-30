package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.dto.AddSolarInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class SolarInfoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private AddSolarInfoDTO solarInfoDTO;

    @BeforeEach
    void setup_add_solarInfo() throws Exception {
        solarInfoDTO = new AddSolarInfoDTO(
                "Budapest",
                "hu",
                null,
                "2024-07-25",
                47.4979,
                19.0402,
                "05:30:11",
                "08:30:22"
        );

        mockMvc.perform(post("/api/admin/solar-info/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solarInfoDTO))
                        .with(user("adminUser").password("asd").roles("ADMIN")))
                .andExpect(status().isCreated());
    }

    @Test
    void getSolarInfo_whenNoAuth_thenReturnsUnauthorized() throws Exception {
        String city = "Budapest";
        String country = "hu";
        String date = "2024-07-25";

        mockMvc.perform(get("/api/solar-info")
                        .param("city", city)
                        .param("country", country)
                        .param("date", date)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());  // Expecting 401 Unauthorized
    }

    @Test
    @WithMockUser(roles = "USER")
    void getSolarInfo_whenUserRole_thenReturnsOk() throws Exception {
        String city = "Budapest";
        String country = "hu";
        String date = "2024-07-25";

        mockMvc.perform(get("/api/solar-info")
                        .param("city", city)
                        .param("country", country)
                        .param("date", date)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Budapest"))
                .andExpect(jsonPath("$[0].country").value("HU"))
                .andExpect(jsonPath("$[0].date").value("2024-07-25"))
                .andExpect(jsonPath("$[0].latitude").value(47.4979))
                .andExpect(jsonPath("$[0].longitude").value(19.0402))
                .andExpect(jsonPath("$[0].sunrise").value("05:30:11"))
                .andExpect(jsonPath("$[0].sunset").value("08:30:22"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void addSolarInfo_whenUserRole_thenReturnsForbidden() throws Exception {
        AddSolarInfoDTO newSolarInfoDTO = new AddSolarInfoDTO(
                "Debrecen",
                "Hungary",
                null,
                "2024-08-01",
                47.5316,
                21.6273,
                "05:20:33",
                "08:20:44"
        );

        mockMvc.perform(post("/api/admin/solar-info/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSolarInfoDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addSolarInfo_whenAdminRole_thenCreatesInfo() throws Exception {
        AddSolarInfoDTO newSolarInfoDTO = new AddSolarInfoDTO(
                "Debrecen",
                "Hungary",
                null,
                "2024-08-01",
                47.5316,
                21.6273,
                "05:20:33",
                "08:20:44"
        );

        mockMvc.perform(post("/api/admin/solar-info/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSolarInfoDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void getAllSolarInfo_whenNoAuthentication_thenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/solar-info/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllSolarInfo_whenUserRole_thenReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/solar-info/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllSolarInfo_whenAdminRole_thenReturnsListOfSolarInfo() throws Exception {
        mockMvc.perform(get("/api/admin/solar-info/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].country").exists())
                .andExpect(jsonPath("$[0].date").exists());
    }
}