package com.codecool.solarwatch.service;

import com.codecool.solarwatch.exceptionhandler.ConflictException;
import com.codecool.solarwatch.model.entity.Client;
import com.codecool.solarwatch.model.payload.JwtResponse;
import com.codecool.solarwatch.model.payload.LoginRequest;
import com.codecool.solarwatch.model.payload.RegistrationRequest;
import com.codecool.solarwatch.repository.ClientRepository;
import com.codecool.solarwatch.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerClient_whenUsernameExists_thenThrowConflictException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("existingUser");
        request.setEmail("email@example.com");

        when(clientRepository.existsByUsername(request.getUsername())).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            clientService.registerClient(request);
        });

        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void registerClient_whenEmailExists_thenThrowConflictException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("newUser");
        request.setEmail("existing@example.com");

        when(clientRepository.existsByEmail(request.getEmail())).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            clientService.registerClient(request);
        });

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void registerClient_whenBothUsernameAndEmailExist_thenThrowConflictException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("existingUser");
        request.setEmail("existing@example.com");

        when(clientRepository.existsByUsername(request.getUsername())).thenReturn(true);
        when(clientRepository.existsByEmail(request.getEmail())).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            clientService.registerClient(request);
        });

        assertEquals("Email already exists; Username already exists", exception.getMessage());
    }

    @Test
    void registerClient_whenNewClient_thenSaveClient() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("newUser");
        request.setEmail("new@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));

        when(clientRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(clientRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(encoder.encode(request.getPassword())).thenReturn("encodedPassword");

        clientService.registerClient(request);

        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void authenticateUser_whenValidCredentials_thenReturnJwtResponse() {
        LoginRequest request = new LoginRequest();
        request.setUsername("validUser");
        request.setPassword("validPassword");

        Authentication authentication = mock(Authentication.class);
        User userDetails = new User("validUser", "password", Collections.emptyList());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwtToken");

        JwtResponse response = clientService.authenticateUser(request);

        assertEquals("jwtToken", response.jwt());
        assertEquals("validUser", response.username());
        assertEquals(Collections.emptyList(), response.roles());
    }

    @Test
    void getAdminUsername_whenAuthenticated_thenReturnAdminUsername() {
        User userDetails = new User("adminUser", "password", Collections.emptyList());
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String result = clientService.getAdminUsername();

        assertEquals("CI works test \nAdmin Endpoint Accessed by admin: adminUser", result);
    }

    @Test
    void getUserUsername_whenAuthenticated_thenReturnUserUsername() {
        User userDetails = new User("normalUser", "password", Collections.emptyList());
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String result = clientService.getUserUsername();

        assertEquals("Secured Endpoint Accessed by user: normalUser", result);
    }
}
