package com.codecool.solarwatch.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.format.DateTimeParseException;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(CityNotFoundInternalException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleInternalLocationNotFoundException(Exception ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(SolarInfoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleSolarInfoNotFoundException(Exception ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDateTimeParseException(Exception ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(Exception ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(CityNotFoundExternalException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleExternalLocationNotFoundException(Exception ex) {
        return ex.getMessage();
    }


    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String, String> handleConflictException(ConflictException ex) {
        return Map.of("error", ex.getMessage());
    }
}
