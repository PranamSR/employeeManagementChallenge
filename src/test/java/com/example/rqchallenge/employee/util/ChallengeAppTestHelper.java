package com.example.rqchallenge.employee.util;

import com.example.rqchallenge.employee.dto.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ChallengeAppTestHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Employee pran() {
        Employee pran = new Employee();
        pran.setId(1L);
        pran.setEmployeeName("Pranam Raghuram");
        pran.setEmployeeAge(32);
        pran.setEmployeeSalary(30000);
        return pran;
    }

    public static Employee hemant() {
        Employee hemant = new Employee();
        hemant.setId(2L);
        hemant.setEmployeeName("Hemant Bhat");
        hemant.setEmployeeAge(28);
        hemant.setEmployeeSalary(190000);
        return hemant;
    }

    public static Employee sanjay() {
        Employee sanjay= new Employee();
        sanjay.setId(3L);
        sanjay.setEmployeeName("Sanjay Iyer");
        sanjay.setEmployeeAge(55);
        sanjay.setEmployeeSalary(130000);
        return sanjay;
    }

    public static Employee mangilal() {
        Employee mangilal = new Employee();
        mangilal.setId(4L);
        mangilal.setEmployeeName("Mangilal Singh");
        mangilal.setEmployeeAge(40);
        mangilal.setEmployeeSalary(200000);
        return mangilal;
    }

    public static Employee pranitha() {
        Employee pranitha = new Employee();
        pranitha.setId(5L);
        pranitha.setEmployeeName("Pranitha Nayak");
        pranitha.setEmployeeAge(39);
        pranitha.setEmployeeSalary(330000);
        return pranitha;
    }

    public static Employee rani() {
        Employee rani = new Employee();
        rani.setId(6L);
        rani.setEmployeeName("Rani Chadda");
        rani.setEmployeeAge(28);
        rani.setEmployeeSalary(90000);
        return rani;
    }

    public static List<String> topTenEmployeeNames() {
        List<String> topTenEmployees = List.of(
                "Shane Warner",
                "Alex Stain",
                "Taylor swift",
                "Ted Mosbey",
                "Yuri Gagarin",
                "Mark Tyson",
                "Harry Potter",
                "Mary cooper",
                "David Litt",
                "Naruto Uzumaki"
        );

        return topTenEmployees;
    }

    public static List<Employee> getAllEmployees() {
        final String fileContents = readFile("data/allEmployees.json");
        List<Employee> employees = null;
        try {
            employees = objectMapper
                                .readerForListOf(Employee.class)
                                .readValue(fileContents);
        } catch (JsonProcessingException e) {
            log.error("Getting Exception while parsing file contents.");
        }
        return employees;
    }

    private static String readFile(String fileName) {
        String jsonString = "";
        try {
            File file = new ClassPathResource(fileName).getFile();
            jsonString = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException ioException) {
            log.error("Getting Exception while getting and reading file.");
        }
        return jsonString;
    }

    public static String getAllEmployeesResponseFromApi() {
        return readFile("data/allEmployeesResponseFromApi.json");
    }

    public static String getAllEmployeeByIdFromApi() {
        return readFile("data/getEmployeeByIdApiResponse.json");
    }

    public static String deleteEmployeeByIdResponseFromApi() {
        return readFile("data/deleteEmployeeByIdApiResponse.json");
    }

    public static String createEmployeeResponseFromApi() {
        return readFile("data/createEmployeeApiResponse.json");
    }

    public static String createEmployeeApiRequest() throws JsonProcessingException {
        Map<String ,Object> requestMap = new HashMap<>();
        requestMap.put("name", "Pranam Raghuram");
        requestMap.put("salary", 20000);
        requestMap.put("age", 21);
        return objectMapper.writeValueAsString(requestMap);
    }
}
