package com.example.rqchallenge.employee.validator;

import com.example.rqchallenge.employee.exception.BadRequestException;
import com.example.rqchallenge.employee.util.AppConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class CreateEmployeeValidator {

    public static void validate(Map<String,Object> employeeInput) {
        if(!employeeInput.containsKey("name")
                || !employeeInput.containsKey("salary")
                    || !employeeInput.containsKey("age")) {
            log.info("[Validate]: Missing fields , Please check request once.");
            throw new BadRequestException(AppConstants.BAD_INPUT_EXCEPTION_MESSAGE);
        }
    }
}
