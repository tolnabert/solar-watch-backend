package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.dto.SolarInfoDTO;
import com.codecool.solarwatch.service.SolarWatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class SolarInfoController {

    private final SolarWatchService solarWatchService;
    private static final Logger LOG = LoggerFactory.getLogger(SolarInfoController.class);

    public SolarInfoController(SolarWatchService solarWatchService) {
        this.solarWatchService = solarWatchService;
    }

    @GetMapping("/solar-info")
    public Set<SolarInfoDTO> getSolarInfo(@RequestParam String city,
                                          @RequestParam String country,
                                          @RequestParam(required = false) String state,
                                          @RequestParam String date

    ) {
        LOG.info("Received request to get solar info for city {}", city);
        return solarWatchService.getSolarInfo(city, country, state, date);
    }
}
