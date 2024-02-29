package com.example.rqchallenge.employee.api.integration;

import com.example.rqchallenge.employee.dto.Employee;
import com.example.rqchallenge.employee.exception.ApiResponseJsonParseException;
import com.example.rqchallenge.employee.exception.InternalServerError;
import com.example.rqchallenge.employee.exception.TooManyRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component("createEmployeeClient")
public class CreateEmployee implements RestApiServiceClient<Employee, Map<String, Object>> {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final String createEmployeeApiPath = "/api/v1/create";

    @Override
    public Employee execute(Map<String, Object> request) {
        Employee employee = webClientBuilder.build().post()
                .uri(uriBuilder -> uriBuilder.path(createEmployeeApiPath).build())
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.value() == 429,
                        clientResponse -> Mono.just(new TooManyRequestException())
                )
                .onStatus(
                        HttpStatus::is5xxServerError,
                        clientResponse -> Mono.just(new InternalServerError(clientResponse.rawStatusCode()))
                )
                .toEntity(String.class)
                .map(apiResponse -> {
                    Employee employeeObject = new Employee();
                    try {
                        JsonNode apiResponseNode = mapper.readTree(apiResponse.getBody());
                        JsonNode dataNode = apiResponseNode.at("/data");
                        
                        employeeObject.setId(dataNode.get("id").asLong());
                        employeeObject.setEmployeeName(dataNode.get("name").asText());
                        employeeObject.setEmployeeAge(dataNode.get("age").asInt());
                        employeeObject.setEmployeeSalary(dataNode.get("salary").asInt());
                    } catch (JsonProcessingException e) {
                        throw new ApiResponseJsonParseException();
                    }

                    return employeeObject;
                })
                .block();
        return employee;
    }
}
