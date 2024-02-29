package com.example.rqchallenge.employee.api.integration;

import com.example.rqchallenge.employee.exception.ApiResponseJsonParseException;
import com.example.rqchallenge.employee.exception.InternalServerError;
import com.example.rqchallenge.employee.exception.TooManyRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;

@Component("deleteEmployeeByIdClient")
public class DeleteEmployeeById implements RestApiServiceClient<String, String> {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final String deleteEmployeeApiPath = "/api/v1/delete/{id}";

    @Override
    public String execute(String empId) {
        String status = webClientBuilder.build().delete()
                .uri(uriBuilder -> uriBuilder.path(deleteEmployeeApiPath).build(empId))
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.value() == 429,
                        apiResponse -> Mono.just(new TooManyRequestException())
                )
                .onStatus(
                        HttpStatus::isError,
                        apiResponse -> Mono.error(new InternalServerError(apiResponse.statusCode().value()))
                )
                .toEntity(String.class)
                .map(apiResponse -> {

                    LinkedHashMap<String,String> responseAsKVPair;
                    try {
                        responseAsKVPair = mapper.readValue(apiResponse.getBody(), new TypeReference<>(){});
                    } catch (JsonProcessingException e) {
                        throw new ApiResponseJsonParseException();
                    }
                    return responseAsKVPair.get("status");
                })
                .block();
        return status;
    }
}
