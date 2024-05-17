package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.dto.CityDTO;
import com.codecool.solarwatch.model.dto.SolarInfoDTO;
import com.codecool.solarwatch.service.SolarWatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/solar-info/data")
public class SolarInfoController {

    private final SolarWatchService solarWatchService;
    private static final Logger LOG = LoggerFactory.getLogger(SolarInfoController.class);

    public SolarInfoController(SolarWatchService solarWatchService) {
        this.solarWatchService = solarWatchService;
    }

    @GetMapping("/{city}/{date}")
    public Set<SolarInfoDTO> getSolarInfo(@PathVariable String city,
                                          @PathVariable String date,
                                          @RequestParam(defaultValue = "1") int limit) {
        LOG.info("Received request to get sunrise and sunset for city {}", city);
        return solarWatchService.getSolarInfo(city, date, limit);
    }

//    @GetMapping("/all")
//    public Set<SolarInfoDTO> getSolarInfo() {
//        return solarWatchService.getAllSolarInfo();
//    }

    @PostMapping("/admin/{date}")
    public void addSolarInfo(@RequestBody SolarInfoDTO solarInfoDTO,
                             @PathVariable String date) {
        solarWatchService.addSolarInfo(solarInfoDTO, date);
    }

    @PutMapping("/admin/{id}")
    public SolarInfoDTO updateSolarInfo(@PathVariable UUID id, @RequestBody SolarInfoDTO solarInfoDTO, @RequestParam(required = false) String date) {
        SolarInfoDTO updatedSolarInfoDTO;
        if (date != null) {
            updatedSolarInfoDTO = solarWatchService.updateSolarInfo(id, solarInfoDTO, date);
        } else {
            updatedSolarInfoDTO = solarWatchService.updateSolarInfo(id, solarInfoDTO);
        }

        LOG.info("Updated SolarInfo with ID {}: {}", id, updatedSolarInfoDTO);

        return updatedSolarInfoDTO;
    }

    @DeleteMapping("/admin/{id}")
    public boolean deleteSolarInfo(@PathVariable UUID id) {
        return solarWatchService.deleteSolarInfo(id);
    }

    @PutMapping("/admin/{city}/{id}")
    public CityDTO updateCity(@PathVariable String city, @PathVariable UUID id, @RequestBody CityDTO updatedCityDTO) {
        return solarWatchService.updateCity(city, id, updatedCityDTO);
    }
}
