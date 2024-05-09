package com.codecool.solarwatch.model.payload;

import lombok.Data;

@Data
public class CreateClientRequest {
    private String username;
    private String password;
}