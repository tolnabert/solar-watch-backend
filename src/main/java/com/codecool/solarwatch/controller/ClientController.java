package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.payload.LoginRequest;
import com.codecool.solarwatch.model.payload.RegistrationRequest;
import com.codecool.solarwatch.model.payload.JwtResponse;
import com.codecool.solarwatch.service.ClientService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/register")
    //TODO send httpstatuscode
    public void createUser(@RequestBody RegistrationRequest registrationRequest) {
        clientService.registerClient(registrationRequest);
    }

    @PostMapping("/login")
    public JwtResponse authenticateUser(@RequestBody LoginRequest loginRequest) {
        return clientService.authenticateUser(loginRequest);
    }

    @GetMapping("/test/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String testMe() {
        return clientService.getAdminUsername();
    }

    @GetMapping("/test/public")
    public String testEndpoint() {
        return "This is a public endpoint!";
    }
}
