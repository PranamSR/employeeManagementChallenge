package com.example.rqchallenge.employee.controller.impl;

import com.example.rqchallenge.employee.controller.IEmployeeController;
import com.example.rqchallenge.employee.dto.Employee;
import com.example.rqchallenge.employee.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("employee")
@Slf4j
/*
 * Controller for Employee Management APIs 
 */
public class EmployeeController implements IEmployeeController {

    @Autowired
    private IEmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("[GetAllEmployees ]: Entering Controller");
        final List<Employee> allEmployees = employeeService.getAllEmployees();
        log.info("[GetAllEmployees ]: Exiting Controller");
        return ResponseEntity.ok(allEmployees);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        log.info("[GetEmployeesByNameSearch]: Entering Controller with searchString {} ", searchString);
        List<Employee> filteredEmployees = employeeService.getEmployeesByNameSearch(searchString);
        log.info("[GetEmployeesByNameSearch]: Exiting Controller with filteredEmployees {}", filteredEmployees);
        return ResponseEntity.ok(filteredEmployees);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        log.info("[GetEmployeeById]: Entering Controller");
        log.info("[GetEmployeeById]: Employee Id {}", id);
        Employee employee = employeeService.getEmployeeById(id);
        log.info("[GetEmployeeById]: Exiting Controller");
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("[GetHighestSalaryOfEmployees]: Entering Controller");
        final Integer highestSalaryOfEmployees = employeeService.getHighestSalaryOfEmployees();
        log.info("[GetHighestSalaryOfEmployees]: Exiting Controller");
        return ResponseEntity.ok(highestSalaryOfEmployees);
    }

    @Override
    public ResponseEntity<List<String>> getTop10HighestEarningEmployeeNames() {
        log.info("[GetTopTenHighestEarningEmployeeNames]: Entering Controller");
        final List<String> topTenHighestEarningEmployeeNames = employeeService.getTop10HighestEarningEmployeeNames();
        log.info("[GetTopTenHighestEarningEmployeeNames]: Exiting Controller");
        return ResponseEntity.ok(topTenHighestEarningEmployeeNames);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
        log.info("[CreateEmployee]: Entering Controller");
        final Employee employee = employeeService.createEmployee(employeeInput);
        log.info("[CreateEmployee]: Exiting Controller");
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        log.info("[DeleteEmployeeById]: Entering Controller");
        final String status = employeeService.deleteEmployee(id);
        log.info("[DeleteEmployeeById]: Exiting Controller");
        return ResponseEntity.ok(status);
    }
}
