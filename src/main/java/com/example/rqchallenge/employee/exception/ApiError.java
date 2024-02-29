package com.example.rqchallenge.employee.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.LocalDateTime;

@Data
public class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private String message;

    public ApiError() {}

    public ApiError(HttpStatus badRequest, HttpMessageNotReadableException ex) {
        this.timestamp = LocalDateTime.now();
        this.status = badRequest.value();
        this.message = ex.getMessage();
    }
}
