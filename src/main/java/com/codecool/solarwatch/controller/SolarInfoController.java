package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.solarinfo.SolarInfoDTO;
import com.codecool.solarwatch.service.SolarInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/solar-info")
public class SolarInfoController {

    private final SolarInfoService solarWatchService;
    private static final Logger LOG = LoggerFactory.getLogger(SolarInfoController.class);

    public SolarInfoController(SolarInfoService solarWatchService) {
        this.solarWatchService = solarWatchService;
    }

    @GetMapping("/{city}/{date}")
    public Set<SolarInfoDTO> getSolarInfo(@PathVariable String city,
                                          @PathVariable String date,
                                          @RequestParam(defaultValue = "1") int limit) {
        LOG.info("Received request to get sunrise and sunset for city {}", city);
        return solarWatchService.getSolarInfo(city, date, limit);
    }
}
