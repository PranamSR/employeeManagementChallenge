package com.example.rqchallenge.employee.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

    protected LocalDateTime timestamp;
    protected int status;
    protected String message;
}