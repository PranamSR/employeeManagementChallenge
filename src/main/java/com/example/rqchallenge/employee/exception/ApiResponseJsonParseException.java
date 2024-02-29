package com.example.rqchallenge.employee.exception;

import com.example.rqchallenge.employee.util.AppConstants;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ApiResponseJsonParseException extends BaseException {

    public ApiResponseJsonParseException() {
        this.timestamp = LocalDateTime.now();
        this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.message = AppConstants.JSON_PARSING_EXCEPTION_MESSAGE;
    }
}
