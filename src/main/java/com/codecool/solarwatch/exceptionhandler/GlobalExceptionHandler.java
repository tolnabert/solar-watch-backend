package com.codecool.solarwatch.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleLocationNotFoundException(Exception ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(NoTwilightDataException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleSunriseSunsetDataException(Exception ex) {
        return ex.getMessage();
    }
}
