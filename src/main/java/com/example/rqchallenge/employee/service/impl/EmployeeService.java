package com.example.rqchallenge.employee.service.impl;

import com.example.rqchallenge.employee.api.integration.RestApiServiceClient;
import com.example.rqchallenge.employee.dto.Employee;
import com.example.rqchallenge.employee.exception.EmployeeNotFoundException;
import com.example.rqchallenge.employee.service.IEmployeeService;
import com.example.rqchallenge.employee.validator.CreateEmployeeValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
/*
 * Service code to handle the API processing
 */
public class EmployeeService implements IEmployeeService {

    @Autowired
    @Qualifier("getAllEmployeesClient")
    private RestApiServiceClient<List<Employee>, Void> getAllEmployeesClient;

    @Autowired
    @Qualifier("createEmployeeClient")
    private RestApiServiceClient<Employee, Map<String, Object>> createEmployeeClient;

    @Autowired
    @Qualifier("getEmployeeByIdClient")
    private RestApiServiceClient<Optional<Employee>, String> getEmployeeByIdClient;

    @Autowired
    @Qualifier("deleteEmployeeByIdClient")
    private RestApiServiceClient<String, String> deleteEmployeeByIdClient;

    @Override
    public List<Employee> getAllEmployees() {
        log.info("[GetAllEmployees]: Entering Service ");
        List<Employee> employees = getAllEmployeesClient.execute(null);
        log.info("[GetAllEmployees]: Fetched {} Employees", employees.size());
        log.info("[GetAllEmployees]: Exiting Service ");
        return employees;
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        log.info("[GetEmployeesByNameSearch]: Entering Service ");

        final List<Employee> allEmployees = getAllEmployees();
        final List<Employee> filteredEmployees = allEmployees.stream()
                .filter(employee ->
                                StringUtils.containsIgnoreCase(employee.getEmployeeName(), searchString))
                .collect(Collectors.toList());

        log.info("[GetEmployeesByNameSearch]: Filtered Employees size based on searchString {}", filteredEmployees.size());
        log.info("[GetEmployeesByNameSearch]: Filtered Employees based on searchString {}", filteredEmployees);
        log.info("[GetEmployeesByNameSearch]: Exiting Service");

        return filteredEmployees;
    }

    @Override
    public Employee getEmployeeById(String empId) {
        log.info("[GetEmployeeById]: Entering Service ");
        log.info("[GetEmployeeById]: Searching employee by Id {}", empId);

        Optional<Employee> optionalEmployee = getEmployeeByIdClient.execute(empId);

        if(!optionalEmployee.isPresent()) {
            log.error("[GetEmployeeById]: Employee Not Found with id {}", empId);
            throw new EmployeeNotFoundException(empId);
        }

        log.info("[GetEmployeeById]: Employee found : {}", optionalEmployee.get());
        log.info("[GetEmployeeById]: Exiting Service ");
        return optionalEmployee.get();
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        log.info("[GetHighestSalaryOfEmployees]: Entering Service ");

        List<Employee> employees = getAllEmployees();
        Integer highestSalary = findHighestSalary(employees);

        log.info("[GetHighestSalaryOfEmployees]: Highest Salary found {}", highestSalary);
        log.info("[GetHighestSalaryOfEmployees]: Exiting Service ");
        return highestSalary;
    }

    private Integer findHighestSalary(List<Employee> employees) {
        Integer maxSalary = Integer.MIN_VALUE;
        for (Employee employee: employees) {
            if(employee.getEmployeeSalary() > maxSalary)
                maxSalary = employee.getEmployeeSalary();
        }

        return maxSalary;
    }

    @Override
    public List<String> getTop10HighestEarningEmployeeNames() {
        log.info("[GetTopTenHighestEarningEmployeeNames]: Entering Service ");

        List<Employee> employees = getAllEmployees();
        final List<String> topTenHighestEarningEmployeeNames = findTopTenHighestEarningEmployeeNames(employees);

        log.info("[GetTopTenHighestEarningEmployeeNames]: top Ten Employees {}", topTenHighestEarningEmployeeNames);
        log.info("[GetTopTenHighestEarningEmployeeNames]: Exiting Service ");
        return topTenHighestEarningEmployeeNames;
    }

    private List<String> findTopTenHighestEarningEmployeeNames(List<Employee> employees) {
        PriorityQueue<Employee> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(Employee::getEmployeeSalary));

        for (Employee employee : employees) {
            priorityQueue.add(employee);

            if (priorityQueue.size() > 10) {
                priorityQueue.poll();
            }
        }

        List<String> employeeNames = priorityQueue.stream().map(Employee::getEmployeeName).collect(Collectors.toList());
        Collections.reverse(employeeNames);
        return employeeNames;
    }

    @Override
    public Employee createEmployee(Map<String, Object> employeeInput) {
        log.info("[CreateEmployee]: Entering Service ");
        // Validate data received before adding
        CreateEmployeeValidator.validate(employeeInput);
        final Employee employee = createEmployeeClient.execute(employeeInput);
        log.info("[CreateEmployee]: Employee Created {}", employee);
        log.info("[CreateEmployee]: Exiting Service ");
        return employee;
    }

    @Override
    public String deleteEmployee(String empId) {
        log.info("[DeleteEmployee]: Entering Service ");
        final Employee employee = getEmployeeById(empId);
        String status = deleteEmployeeByIdClient.execute(empId);

        log.info("[DeleteEmployee]: Employee deletion status {}", status);

        final String employeeName = employee.getEmployeeName();

        log.info("[DeleteEmployee]: Employee Name {}", employeeName);
        log.info("[DeleteEmployee]: Exiting Service ");
        return employeeName;
    }
}
