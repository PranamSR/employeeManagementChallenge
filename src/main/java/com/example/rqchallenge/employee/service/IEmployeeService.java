package com.example.rqchallenge.employee.service;

import com.example.rqchallenge.employee.dto.Employee;

import java.util.List;
import java.util.Map;

public interface IEmployeeService {

    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String searchString);

    Employee getEmployeeById(String id);

    Integer getHighestSalaryOfEmployees();

    List<String> getTop10HighestEarningEmployeeNames();

    Employee createEmployee(Map<String, Object> employeeInput);

    String deleteEmployee(String id);
}
