package com.example.rqchallenge.employee.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class BadRequestException extends BaseException {

    public BadRequestException(String message) {
        this.timestamp = LocalDateTime.now();
        this.status = HttpStatus.BAD_REQUEST.value();
        this.message = message;
    }
}
