package com.example.rqchallenge.employee.exception;

import com.example.rqchallenge.employee.util.AppConstants;

import java.time.LocalDateTime;

public class InternalServerError extends BaseException {

    public InternalServerError(int statusCode) {
        this.timestamp = LocalDateTime.now();
        this.status = statusCode;
        this.message = AppConstants.INTERNAL_SERVER_ERROR_OCCURRED_MESSAGE;
    }
}
