package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.dto.AddSolarInfoDTO;
import com.codecool.solarwatch.model.dto.SolarInfoDTO;
import com.codecool.solarwatch.service.SolarWatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SolarInfoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SolarWatchService solarWatchService;

    @Autowired
    private ObjectMapper objectMapper;

    private AddSolarInfoDTO addSolarInfoDTO;
    private SolarInfoDTO solarInfoDTO;

    @BeforeEach
    public void setUp() {
        addSolarInfoDTO = new AddSolarInfoDTO(
                "Test City",
                "Test Country",
                "Test State",
                "2023-07-23",
                45.0,
                35.0,
                "06:00:00",
                "18:00:00"
        );

        solarInfoDTO = new SolarInfoDTO(
                "Test City",
                "Test Country",
                "Test State",
                "2023-07-23",
                45.0,
                35.0,
                "06:00:00",
                "18:00:00"
        );
    }

    @Test
    public void testGetSolarInfo() throws Exception {
        Set<SolarInfoDTO> solarInfoSet = new HashSet<>(Collections.singletonList(solarInfoDTO));
        Mockito.when(solarWatchService.getSolarInfo("Test City", "Test Country", "Test State", "2023-07-23"))
                .thenReturn(solarInfoSet);

        mockMvc.perform(get("/api/solar-info")
                        .param("city", "Test City")
                        .param("country", "Test Country")
                        .param("state", "Test State")
                        .param("date", "2023-07-23"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Test City")))
                .andExpect(jsonPath("$[0].country", is("Test Country")))
                .andExpect(jsonPath("$[0].state", is("Test State")))
                .andExpect(jsonPath("$[0].date", is("2023-07-23")))
                .andExpect(jsonPath("$[0].latitude", is(45.0)))
                .andExpect(jsonPath("$[0].longitude", is(35.0)))
                .andExpect(jsonPath("$[0].sunrise", is("06:00:00")))
                .andExpect(jsonPath("$[0].sunset", is("18:00:00")));
    }

    @Test
    public void testAddSolarInfo() throws Exception {
        mockMvc.perform(post("/api/admin/solar-info/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addSolarInfoDTO)))
                .andExpect(status().isCreated());

        Mockito.verify(solarWatchService, Mockito.times(1)).addSolarInfo(any(AddSolarInfoDTO.class));
    }

    @Test
    public void testGetAllSolarInfo() throws Exception {
        Set<SolarInfoDTO> solarInfoSet = new HashSet<>(Collections.singletonList(solarInfoDTO));
        Mockito.when(solarWatchService.getAllSolarInfo()).thenReturn(solarInfoSet);

        mockMvc.perform(get("/api/admin/solar-info/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Test City")))
                .andExpect(jsonPath("$[0].country", is("Test Country")))
                .andExpect(jsonPath("$[0].state", is("Test State")))
                .andExpect(jsonPath("$[0].date", is("2023-07-23")))
                .andExpect(jsonPath("$[0].latitude", is(45.0)))
                .andExpect(jsonPath("$[0].longitude", is(35.0)))
                .andExpect(jsonPath("$[0].sunrise", is("06:00:00")))
                .andExpect(jsonPath("$[0].sunset", is("18:00:00")));
    }
}