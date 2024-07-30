package com.codecool.solarwatch.service;

import com.codecool.solarwatch.exceptionhandler.ConflictException;
import com.codecool.solarwatch.model.entity.Client;
import com.codecool.solarwatch.model.entity.Role;
import com.codecool.solarwatch.model.payload.JwtResponse;
import com.codecool.solarwatch.model.payload.LoginRequest;
import com.codecool.solarwatch.model.payload.RegistrationRequest;
import com.codecool.solarwatch.repository.ClientRepository;
import com.codecool.solarwatch.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public ClientService(ClientRepository clientRepository, PasswordEncoder encoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.clientRepository = clientRepository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public void registerClient(RegistrationRequest registrationRequest) {
        List<String> errors = new ArrayList<>();

        if (clientRepository.existsByUsername(registrationRequest.getUsername())) {
            errors.add("Username already exists");
        }

        if (clientRepository.existsByEmail(registrationRequest.getEmail())) {
            errors.add("Email already exists");
        }

        if (!errors.isEmpty()) {
            List<String> sortedErrors = new ArrayList<>(errors);
            sortedErrors.sort(Comparator.naturalOrder());

            throw new ConflictException(String.join("; ", sortedErrors));
        }

        Client client = new Client();
        client.setPublicId(UUID.randomUUID());
        client.setFirstName(registrationRequest.getFirstName());
        client.setLastName(registrationRequest.getLastName());
        client.setDateOfBirth(registrationRequest.getDateOfBirth());
        client.setEmail(registrationRequest.getEmail());
        client.setUsername(registrationRequest.getUsername());
        client.setPassword(encoder.encode(registrationRequest.getPassword()));
        client.setRoles(Set.of(Role.ROLE_USER));

        clientRepository.save(client);
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .toList();
        return new JwtResponse(jwt, userDetails.getUsername(), roles);
    }

    public String getAdminUsername() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "Admin Endpoint Accessed by admin: " + user.getUsername();
    }

    public String getUserUsername() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "Secured Endpoint Accessed by user: " + user.getUsername();
    }
}
