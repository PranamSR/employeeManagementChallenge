package com.example.rqchallenge.employee.api.integration;

import com.example.rqchallenge.employee.dto.Employee;
import com.example.rqchallenge.employee.exception.ApiResponseJsonParseException;
import com.example.rqchallenge.employee.exception.InternalServerError;
import com.example.rqchallenge.employee.exception.TooManyRequestException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component("getEmployeeByIdClient")
public class GetEmployeeById implements RestApiServiceClient<Optional<Employee>, String> {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final String getEmployeeByIdApiPath = "/api/v1/employee/{id}";

    @Override
    public Optional<Employee> execute(String empId) {

        Optional<Employee> employee = webClientBuilder.build().get()
                .uri(uriBuilder -> uriBuilder.path(getEmployeeByIdApiPath).build(empId))
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.value() == 429,
                        response -> Mono.just(new TooManyRequestException())
                )
                .onStatus(
                        HttpStatus::is5xxServerError,
                        response -> Mono.just(new InternalServerError(response.statusCode().value()))
                )
                .toEntity(String.class)
                .map(apiResponse -> parse(apiResponse.getBody()))
                .block();
        return employee;
    }

    private Optional<Employee> parse(String apiResponse) {
        Employee employeeObject;
        try {
            JsonNode apiResponseNode = mapper.readTree(apiResponse);
            JsonNode dataNode = apiResponseNode.at("/data");
            employeeObject = mapper.readerFor(Employee.class).readValue(dataNode);
        } catch (Exception exception) {
            throw new ApiResponseJsonParseException();
        }
        return Optional.ofNullable(employeeObject);
    }
}
