package com.codecool.solarwatch.model.payload;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistrationRequest {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String email;
    private String username;
    private String password;
    private String passwordConfirmation;
}
