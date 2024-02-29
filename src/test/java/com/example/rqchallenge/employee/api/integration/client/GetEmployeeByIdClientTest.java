package com.example.rqchallenge.employee.api.integration.client;

import com.example.rqchallenge.employee.api.integration.RestApiServiceClient;
import com.example.rqchallenge.employee.api.integration.GetEmployeeById;
import com.example.rqchallenge.employee.dto.Employee;
import com.example.rqchallenge.employee.exception.InternalServerError;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class GetEmployeeByIdClientTest {
    private ClientAndServer mockServer;

    private RestApiServiceClient<Optional<Employee>, String> getEmployeeById;

    @BeforeEach
    public void setupMockServer() {
        mockServer = ClientAndServer.startClientAndServer(2001);
        getEmployeeById = new GetEmployeeById();
        ReflectionTestUtils.setField(getEmployeeById, "webClientBuilder", WebClient.builder().baseUrl("http://localhost:" + mockServer.getLocalPort()));
    }

    @Test
    void whenGetEmployeeByIdApi_IfGivesSuccess_thenReturnOptionalEmployee() {
        String employeeByIdResponse = ChallengeAppTestHelper.getAllEmployeeByIdFromApi();

        mockServer.when(
                request()
                        .withMethod(HttpMethod.GET.name())
                        .withPath("/api/v1/employee/1")
        ).respond(
                response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(employeeByIdResponse)
        );

        final Optional<Employee> optionalEmployee = getEmployeeById.execute("1");

        assertThat(optionalEmployee).isNotNull();

        Employee employee = optionalEmployee.get();
        assertThat(employee.getId()).isEqualTo(1);
        assertThat(employee.getEmployeeName()).isEqualTo("Naruto Uzumaki");
        assertThat(employee.getEmployeeAge()).isEqualTo(61);
        assertThat(employee.getEmployeeSalary()).isEqualTo(320800);

        mockServer.verify(
                request().withMethod(HttpMethod.GET.name())
                        .withPath("/api/v1/employee/1")
        );

    }

    @Test
    void whenGetEmployeeByIdApi_IfGivesInternalServerErrorResponse_thenReturnInternalServerErrorResponse() {
        mockServer.when(
                request()
                        .withMethod(HttpMethod.GET.name())
                        .withPath("/api/v1/employee/1")
        ).respond(
                response()
                        .withStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        );

        Assertions.assertThrows(InternalServerError.class, ()-> getEmployeeById.execute("1"));
    }

    @Test
    void whenGetEmployeeByIdApi_IfGivesTooManyRequestErrorResponse_thenReturnTooManyRequestErrorResponse() {
        mockServer.when(
                request()
                        .withMethod(HttpMethod.GET.name())
                        .withPath("/api/v1/employee/1")
        ).respond(
                response()
                        .withStatusCode(HttpStatus.TOO_MANY_REQUESTS.value())
        );

        Assertions.assertThrows(TooManyRequestException.class, ()-> getEmployeeById.execute("1"));
    }

    @AfterEach
    public void tearDownServer() {
        mockServer.stop();
    }
}
