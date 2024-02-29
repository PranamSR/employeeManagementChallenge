package com.example.rqchallenge.employee.exception.handler;

import com.example.rqchallenge.employee.exception.*;
import com.example.rqchallenge.employee.util.AppConstants;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, ex));
    }

    @ExceptionHandler({
            TooManyRequestException.class, EmployeeNotFoundException.class,
            ApiResponseJsonParseException.class, InternalServerError.class,
            BadRequestException.class
    })
    protected ResponseEntity<Object> handleCustomException(
            BaseException ex) {
        ApiError apiError = new ApiError();
        buildApiError(apiError, ex);
        return buildResponseEntity(apiError);
    }

    private void buildApiError(ApiError apiError, BaseException ex) {
        apiError.setTimestamp(ex.getTimestamp());
        apiError.setStatus(ex.getStatus());
        apiError.setMessage(ex.getMessage());
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, HttpStatus.valueOf(apiError.getStatus()));
    }
}
