package com.example.rqchallenge.employee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Employee implements Serializable {

    private Long id;

    @JsonProperty(value = "employee_name")
    private String employeeName;

    @JsonProperty(value = "employee_salary")
    private Integer employeeSalary;

    @JsonProperty(value = "employee_age")
    private Integer employeeAge;

    @JsonProperty(value = "profile_image")
    private String profileImage;

}
