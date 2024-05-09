package com.codecool.solarwatch.model.payload;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

