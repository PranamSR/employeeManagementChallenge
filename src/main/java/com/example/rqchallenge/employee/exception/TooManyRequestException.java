package com.example.rqchallenge.employee.exception;

import com.example.rqchallenge.employee.util.AppConstants;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class TooManyRequestException extends BaseException {

    public TooManyRequestException() {
        this.status = HttpStatus.TOO_MANY_REQUESTS.value();
        this.timestamp = LocalDateTime.now();
        this.message = AppConstants.TOO_MANY_REQUESTS_MESSAGE;
    }
}
