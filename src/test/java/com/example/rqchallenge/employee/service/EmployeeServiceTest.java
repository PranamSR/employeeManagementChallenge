package com.example.rqchallenge.employee.service;

import com.example.rqchallenge.employee.api.integration.RestApiServiceClient;
import com.example.rqchallenge.employee.dto.Employee;
import com.example.rqchallenge.employee.exception.*;
import com.example.rqchallenge.employee.service.impl.EmployeeService;
import com.example.rqchallenge.employee.util.ChallengeAppTestConstants;
import com.example.rqchallenge.employee.util.ChallengeAppTestHelper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static com.example.rqchallenge.employee.util.ChallengeAppTestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmployeeServiceTest {

    @MockBean
    private RestApiServiceClient<Employee, Map<String, Object>> createEmployeeApiService;

    @MockBean
    private RestApiServiceClient<List<Employee>, Void> getAllEmployeesService;

    @MockBean
    private RestApiServiceClient<Optional<Employee>, String> getEmployeeByIdService;

    @MockBean
    private RestApiServiceClient<String, String> deleteEmployeeService;

    @Autowired
    private EmployeeService employeeService;

    @Test
    void givenEmployeeList_whenGetAllEmployees_thenListOfEmployeeShouldBeReturned() {

        Employee pran = pran();
        Employee pranitha = pranitha();

        List<Employee> allEmployees = List.of(pran, pranitha, rani(), sanjay(), mangilal());

        given(getAllEmployeesService.execute(null))
                .willReturn(allEmployees);

        List<Employee> employees = employeeService.getAllEmployees();

        assertThat(employees).isNotNull();
        assertThat(employees).hasSize(5);
        assertThat(employees).containsAll(allEmployees);
        assertThat(employees).extracting("id").contains(pran.getId());
        assertThat(employees).extracting("employeeName").contains(pran.getEmployeeName());
    }

    @Test
    void givenEmptyEmployeeList_whenGetAllEmployees_thenReturnEmptyEmployeeList() {

        given(getAllEmployeesService.execute(null)).willReturn(Collections.emptyList());

        List<Employee> employees = employeeService.getAllEmployees();

        assertThat(employees).isEmpty();
        assertThat(employees).hasSize(0);
    }

    @Test
    void whenGetAllEmployees_thenThrowsTooManyRequests() {
        given(getAllEmployeesService.execute(null)).willThrow(new TooManyRequestException());
        assertThrows(TooManyRequestException.class, () -> employeeService.getAllEmployees());
    }

    @Test
    void whenGetAllEmployees_thenThrowsApiResponseJsonParseException() {
        given(getAllEmployeesService.execute(null)).willThrow(new ApiResponseJsonParseException());
        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.getAllEmployees());
    }

    @Test
    void whenGetEmployeeById_thenReturnEmployee() {
        String empId = "1";
        final Employee pran = pran();
        given(getEmployeeByIdService.execute(empId)).willReturn(Optional.of(pran));

        Employee employeeById = employeeService.getEmployeeById(empId);
        assertThat(employeeById).isNotNull();
        assertThat(employeeById).isEqualTo(pran);

        assertThat(employeeById).extracting("id").isEqualTo(pran.getId());
        assertThat(employeeById).extracting("employeeName").isEqualTo(pran.getEmployeeName());
        assertThat(employeeById).extracting("employeeAge").isEqualTo(pran.getEmployeeAge());
        assertThat(employeeById).extracting("employeeSalary").isEqualTo(pran.getEmployeeSalary());
    }

    @Test
    void whenGetEmployeeById_thenThrowsEmployeeNotFoundException() {
        String empId = "1090";
        given(getEmployeeByIdService.execute(empId)).willThrow(new EmployeeNotFoundException(empId));

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(empId));
    }

    @Test
    void whenGetEmployeeById_thenThrowsTooManyRequestsException() {
        String empId = "1090";
        given(getEmployeeByIdService.execute(empId)).willThrow(new TooManyRequestException());

        assertThrows(TooManyRequestException.class, () -> employeeService.getEmployeeById(empId));
    }

    @Test
    void whenGetEmployeeById_thenThrowsApiResponseJsonParseException() {
        String empId = "1090";
        given(getEmployeeByIdService.execute(empId)).willThrow(new ApiResponseJsonParseException());

        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.getEmployeeById(empId));
    }

    @Test
    void whenGetEmployeesByNameSearch_returnMatchingEmployeeList() {

        Employee pran = pran();
        Employee pranitha = pranitha();

        given(getAllEmployeesService.execute(null)).willReturn(List.of(pran, pranitha, hemant(), rani(), sanjay(), mangilal()));

        List<Employee> filteredEmployees = employeeService.getEmployeesByNameSearch("pr");

        assertThat(filteredEmployees).hasSize(2);
        assertThat(filteredEmployees).contains(pran, pranitha);
        assertThat(filteredEmployees).extracting("id").contains(pran.getId(), pranitha.getId());
        assertThat(filteredEmployees).extracting("employeeName").contains(pran.getEmployeeName(), pranitha.getEmployeeName());
        assertThat(filteredEmployees).extracting("employeeAge").contains(pran.getEmployeeAge(), pranitha.getEmployeeAge());
        assertThat(filteredEmployees).extracting("employeeSalary").contains(pran.getEmployeeSalary(), pranitha.getEmployeeSalary());
    }

    @Test
    void whenGetEmployeesByNameSearch_IfTooManyRequests_throwsTooManyRequestException() {
        given(getAllEmployeesService.execute(null)).willThrow(new TooManyRequestException());

        final String searchString = "pr";

        assertThrows(TooManyRequestException.class, () -> employeeService.getEmployeesByNameSearch(searchString));
    }

    @Test
    void whenGetEmployeesByNameSearch_IfApiResponseParsingFails_throwsApiResponseJsonParseException() {
        given(getAllEmployeesService.execute(null)).willThrow(new ApiResponseJsonParseException());
        final String searchString = "pr";
        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.getEmployeesByNameSearch(searchString));
    }

    @Test
    void whenGetHighestSalaryOfEmployee_IfSuccess_thenReturnHighestSalary() {
        Employee pran = pran();
        Employee pranitha = pranitha();

        given(getAllEmployeesService.execute(null)).willReturn(List.of(pran, pranitha, hemant(), rani(), sanjay(), mangilal()));

        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();

        assertThat(highestSalary).isEqualTo(pranitha.getEmployeeSalary());
    }

    @Test
    void whenGetHighestSalaryOfEmployee_IfTooManyRequests_thenThrowsTooManyRequestException() {
        given(getAllEmployeesService.execute(null)).willThrow(new TooManyRequestException());
        assertThrows(TooManyRequestException.class, () -> employeeService.getHighestSalaryOfEmployees());
    }

    @Test
    void whenGetHighestSalaryOfEmployee_IfApiResponseParsingFails_thenThrowsApiResponseJsonParseException() {
        given(getAllEmployeesService.execute(null)).willThrow(new ApiResponseJsonParseException());
        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.getHighestSalaryOfEmployees());
    }

    @Test
    void whenGetTopTenHighestEarningEmployeeNames_IfSuccess_thenReturnEmployeeNamesList() {
        List<Employee> allEmployees = ChallengeAppTestHelper.getAllEmployees();
        given(getAllEmployeesService.execute(null)).willReturn(allEmployees);

        List<String> employeeNames = employeeService.getTop10HighestEarningEmployeeNames();

        assertThat(employeeNames).hasSize(10);
        assertThat(employeeNames).containsAnyOf("Shane Warner", "Naruto Uzumaki");
    }

    @Test
    void whenGetTopTenHighestEarningEmployeeNames_IfEmptyEmployeesList_thenReturnEmptyEmployeeNamesList() {
        given(getAllEmployeesService.execute(null)).willReturn(new ArrayList<>());

        List<String> employeeNames = employeeService.getTop10HighestEarningEmployeeNames();

        assertThat(employeeNames).hasSize(0);
    }

    @Test
    void whenGetTopTenHighestEarningEmployeeNames_IfTooManyRequests_thenThrowsTooManyRequestException() {
        given(getAllEmployeesService.execute(null)).willThrow(new TooManyRequestException());
        assertThrows(TooManyRequestException.class, () -> employeeService.getTop10HighestEarningEmployeeNames());
    }

    @Test
    void whenGetTopTenHighestEarningEmployeeNames_IfApiResponseParsingFails_thenThrowsApiResponseJsonParseException() {
        given(getAllEmployeesService.execute(null)).willThrow(new ApiResponseJsonParseException());
        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.getTop10HighestEarningEmployeeNames());
    }

    @Test
    void whenCreateEmployee_IfSuccess_thenReturnCreatedEmployee() {
        Map<String, Object> input = new HashMap<>();
        input.put("name", "Pranam Raghuram");
        input.put("age", 32);
        input.put("salary", 30000);

        Employee pran = pran();
        given(createEmployeeApiService.execute(input)).willReturn(pran);

        Employee employee = employeeService.createEmployee(input);

        assertThat(employee).isEqualTo(pran);
        assertThat(employee).extracting("id").isEqualTo(pran.getId());
        assertThat(employee).extracting("employeeName").isEqualTo(pran.getEmployeeName());
        assertThat(employee).extracting("employeeAge").isEqualTo(pran.getEmployeeAge());
        assertThat(employee).extracting("employeeSalary").isEqualTo(pran.getEmployeeSalary());
    }

    @Test
    void whenCreateEmployee_IfInputIsInvalid_thenThrowBadRequestException() {
        Map<String, Object> input = new HashMap<>();
        input.put("name", "Pranam Raghuram");
        input.put("age", 32);

        lenient().when(createEmployeeApiService.execute(input))
                .thenThrow(new BadRequestException(ChallengeAppTestConstants.BAD_INPUT_EXCEPTION_MESSAGE));

        assertThrows(BadRequestException.class, () -> employeeService.createEmployee(input));
    }

    @Test
    void whenCreateEmployee_IfTooManyRequests_thenThrowsTooManyRequestException() {
        Map<String, Object> input = new HashMap<>();
        input.put("name", "Pranam Raghuram");
        input.put("age", 32);
        input.put("salary", 30000);

        when(createEmployeeApiService.execute(input))
                .thenThrow(new TooManyRequestException());

        assertThrows(TooManyRequestException.class, () -> employeeService.createEmployee(input));
    }

    @Test
    void whenCreateEmployee_IfApiResponseParsingFails_thenThrowsApiResponseJsonParseException() {
        Map<String, Object> input = new HashMap<>();
        input.put("name", "Pranam Raghuram");
        input.put("age", 32);
        input.put("salary", 30000);

        given(createEmployeeApiService.execute(input))
                .willThrow(new ApiResponseJsonParseException());

        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.createEmployee(input));
    }
    
    @Test
    void whenDeleteEmployee_IfSuccess_thenGetDeletedEmployeeName() {
        Employee pran = pran();
        Optional<Employee> optionalEmployee = Optional.of(pran);
        final String empId = pran.getId().toString();
        given(getEmployeeByIdService.execute(empId)).willReturn(optionalEmployee);
        given(deleteEmployeeService.execute(empId)).willReturn(pran.getEmployeeName());

        assertThat(employeeService.deleteEmployee(empId)).isEqualTo(pran.getEmployeeName());
    }

    @Test
    void whenDeleteEmployee_IfServerReturnError_thenThrowsInternalServerError() {
        Employee pran = pran();
        Optional<Employee> optionalEmployee = Optional.of(pran);
        final String empId = pran.getId().toString();
        given(getEmployeeByIdService.execute(empId)).willReturn(optionalEmployee);
        given(deleteEmployeeService.execute(empId)).willThrow(new InternalServerError(500));

        assertThrows(InternalServerError.class, () -> employeeService.deleteEmployee(empId));
    }

    @Test
    void whenDeleteEmployee_IfTooManyRequests_thenThrowsTooManyRequestException() {
        Employee pran = pran();
        Optional<Employee> optionalEmployee = Optional.of(pran);
        final String empId = pran.getId().toString();
        given(getEmployeeByIdService.execute(empId)).willReturn(optionalEmployee);
        given(deleteEmployeeService.execute(empId)).willThrow(new TooManyRequestException());

        assertThrows(TooManyRequestException.class, () -> employeeService.deleteEmployee(empId));
    }

    @Test
    void whenDeleteEmployee_IfApiResponseParsingFails_thenThrowsApiResponseJsonParseException() {
        Employee pran = pran();
        Optional<Employee> optionalEmployee = Optional.of(pran);
        final String empId = pran.getId().toString();
        given(getEmployeeByIdService.execute(empId)).willReturn(optionalEmployee);
        given(deleteEmployeeService.execute(empId)).willThrow(new ApiResponseJsonParseException());

        assertThrows(ApiResponseJsonParseException.class, () -> employeeService.deleteEmployee(empId));
    }
}