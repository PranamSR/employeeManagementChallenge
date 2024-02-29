package com.example.rqchallenge.employee.exception;

import com.example.rqchallenge.employee.util.AppConstants;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class EmployeeNotFoundException extends BaseException {

    private static final long serialVersionUID = 1L;

    public EmployeeNotFoundException(String message) {
        this.status = HttpStatus.NOT_FOUND.value();
        this.timestamp = LocalDateTime.now();
        this.message = AppConstants.EMPLOYEE_NOT_FOUND_WITH_ID_MESSAGE + message;
    }
}
