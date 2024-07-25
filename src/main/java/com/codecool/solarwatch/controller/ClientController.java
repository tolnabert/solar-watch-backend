package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.model.payload.LoginRequest;
import com.codecool.solarwatch.model.payload.RegistrationRequest;
import com.codecool.solarwatch.model.payload.JwtResponse;
import com.codecool.solarwatch.service.ClientService;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/register")
    public void createUser(@RequestBody RegistrationRequest registrationRequest) {
        clientService.registerClient(registrationRequest);
    }

    @PostMapping("/login")
    public JwtResponse authenticateUser(@RequestBody LoginRequest loginRequest) {
        return clientService.authenticateUser(loginRequest);
    }

    @GetMapping("/test/user")
    @PreAuthorize("hasRole('USER')")
    public String testUserEndpoint() {
        return clientService.getUserUsername();
    }

    @GetMapping("/test/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String testAdminEndpoint() {
        return clientService.getAdminUsername();
    }

    @GetMapping("/test/public")
    public String testPublicEndpoint() {
        return "This is a public endpoint!";
    }
}
