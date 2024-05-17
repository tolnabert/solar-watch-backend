package com.codecool.solarwatch.model.payload;

import lombok.Data;

@Data // use record, if  field is need to be used with setter use @Data and if needs builder then @Builder
public class CreateClientRequest {
    private String username;
    private String password;
}