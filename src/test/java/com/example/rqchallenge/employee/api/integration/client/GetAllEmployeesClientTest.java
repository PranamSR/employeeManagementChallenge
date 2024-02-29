package com.example.rqchallenge.employee.api.integration.client;

import com.example.rqchallenge.employee.api.integration.RestApiServiceClient;
import com.example.rqchallenge.employee.api.integration.GetAllEmployees;
import com.example.rqchallenge.employee.dto.Employee;
import com.example.rqchallenge.employee.exception.TooManyRequestException;
import com.example.rqchallenge.employee.util.ChallengeAppTestHelper;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class GetAllEmployeesClientTest {

    private ClientAndServer mockServer;

    private RestApiServiceClient<List<Employee>, Void> getAllEmployees;

    @BeforeEach
    public void setupMockServer() {
        mockServer = ClientAndServer.startClientAndServer(2001);
        getAllEmployees = new GetAllEmployees();
        ReflectionTestUtils.setField(getAllEmployees,"webClientBuilder",
                WebClient.builder().baseUrl("http://localhost:" + mockServer.getLocalPort()));
    }

    @Test
    void whenGetAllEmployeesApi_IfGivesSuccess_thenReturnListOfEmployees() {
        String responseBody = ChallengeAppTestHelper.getAllEmployeesResponseFromApi();

        mockServer.when(
                request()
                        .withMethod(HttpMethod.GET.name())
                        .withPath("/api/v1/employees")
        ).respond(
                response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(responseBody)
        );

        final List<Employee> employees = getAllEmployees.execute(null);

        assertThat(employees).hasSize(24);
        assertThat(employees.get(0).getId()).isEqualTo(1L);
        assertThat(employees.get(0).getEmployeeName()).isEqualTo("Naruto Uzumaki");
        assertThat(employees.get(0).getEmployeeSalary()).isEqualTo(320800);
        assertThat(employees.get(0).getEmployeeAge()).isEqualTo(61);

        mockServer.verify(
                request().withMethod(HttpMethod.GET.name())
                        .withPath("/api/v1/employees")
        );

    }

    @Test
    void whenGetAllEmployeesApi_IfGivesErrorResponse_thenReturnErrorResponse() {
        mockServer.when(
                request()
                        .withMethod(HttpMethod.GET.name())
                        .withPath("/api/v1/employees")
        ).respond(
                response()
                        .withStatusCode(HttpStatus.TOO_MANY_REQUESTS.value())
        );

        Assertions.assertThrows(TooManyRequestException.class, ()-> getAllEmployees.execute(null));
    }

    @AfterEach
    public void tearDownServer() {
        mockServer.stop();
    }

}
