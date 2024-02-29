package com.example.rqchallenge.employee.api.integration.client;

import com.example.rqchallenge.employee.api.integration.RestApiServiceClient;
import com.example.rqchallenge.employee.api.integration.CreateEmployee;
import com.example.rqchallenge.employee.dto.Employee;
import com.example.rqchallenge.employee.exception.InternalServerError;
import com.example.rqchallenge.employee.exception.TooManyRequestException;
import com.example.rqchallenge.employee.util.ChallengeAppTestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class CreateEmployeeClientTest {

    private ClientAndServer mockServer;

    private RestApiServiceClient<Employee, Map<String, Object>> createEmployeeClient;

    @BeforeEach
    public void setupMockServer() {
        mockServer = ClientAndServer.startClientAndServer(2001);
        createEmployeeClient = new CreateEmployee();
        ReflectionTestUtils.setField(createEmployeeClient, "webClientBuilder",
                WebClient.builder().baseUrl("http://localhost:" + mockServer.getLocalPort()));
    }

    @Test
    void whenCreateEmployeeApi_IfGivesSuccess_thenReturnListOfEmployees() throws JsonProcessingException {
        mockServer.when(
                request()
                        .withMethod(HttpMethod.POST.name())
                        .withPath("/api/v1/create")
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(ChallengeAppTestHelper.createEmployeeApiRequest())
        ).respond(
                response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(ChallengeAppTestHelper.createEmployeeResponseFromApi())
        );


        Map<String ,Object> requestMap = new HashMap<>();
        requestMap.put("name", "Pranam Raghuram");
        requestMap.put("salary", 20000);
        requestMap.put("age", 21);
        Employee createdEmployee = createEmployeeClient.execute(requestMap);

        assertThat(createdEmployee).isNotNull();
        assertThat(createdEmployee.getEmployeeName()).isEqualTo("Pranam Raghuram");
        assertThat(createdEmployee.getEmployeeAge()).isEqualTo(32);
        assertThat(createdEmployee.getEmployeeSalary()).isEqualTo(20000);
        assertThat(createdEmployee.getId()).isEqualTo(1);

        mockServer.verify(
                request().withMethod(HttpMethod.POST.name())
                        .withPath("/api/v1/create")
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(ChallengeAppTestHelper.createEmployeeApiRequest())
        );

    }

    @Test
    void whenCreateEmployeeApi_IfGivesInternalServerErrorResponse_thenReturnInternalServerErrorResponse() throws JsonProcessingException {
        mockServer.when(
                request()
                        .withMethod(HttpMethod.POST.name())
                        .withPath("/api/v1/create")
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(ChallengeAppTestHelper.createEmployeeApiRequest())
        ).respond(
                response()
                        .withStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        );

        Map<String ,Object> requestMap = new HashMap<>();
        requestMap.put("name", "Pranam Raghuram");
        requestMap.put("salary", 20000);
        requestMap.put("age", 21);
        Assertions.assertThrows(InternalServerError.class, ()-> createEmployeeClient.execute(requestMap));

        mockServer.verify(
                request().withMethod(HttpMethod.POST.name())
                        .withPath("/api/v1/create")
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(ChallengeAppTestHelper.createEmployeeApiRequest())
        );
    }

    @Test
    void whenCreateEmployeeApi_IfGivesTooManyRequestErrorResponse_thenReturnTooManyRequestErrorResponse() throws JsonProcessingException {
        mockServer.when(
                request()
                        .withMethod(HttpMethod.POST.name())
                        .withPath("/api/v1/create")
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(ChallengeAppTestHelper.createEmployeeApiRequest())
        ).respond(
                response()
                        .withStatusCode(HttpStatus.TOO_MANY_REQUESTS.value())
        );

        Map<String ,Object> requestMap = new HashMap<>();
        requestMap.put("name", "Pranam Raghuram");
        requestMap.put("salary", 20000);
        requestMap.put("age", 21);
        Assertions.assertThrows(TooManyRequestException.class, ()-> createEmployeeClient.execute(requestMap));

        mockServer.verify(
                request().withMethod(HttpMethod.POST.name())
                        .withPath("/api/v1/create")
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(ChallengeAppTestHelper.createEmployeeApiRequest())
        );
    }

    @AfterEach
    public void tearDownServer() {
        mockServer.stop();
    }
}
