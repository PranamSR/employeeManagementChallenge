package com.example.rqchallenge.employee.api.integration.client;

import com.example.rqchallenge.employee.api.integration.RestApiServiceClient;
import com.example.rqchallenge.employee.api.integration.DeleteEmployeeById;
import com.example.rqchallenge.employee.exception.InternalServerError;
import com.example.rqchallenge.employee.exception.TooManyRequestException;
import com.example.rqchallenge.employee.util.ChallengeAppTestConstants;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class DeleteEmployeeByIdClientTest {

    private ClientAndServer mockServer;

    private RestApiServiceClient<String, String> deleteEmployeeById;

    @BeforeEach
    public void setupMockServer() {
        mockServer = ClientAndServer.startClientAndServer(2001);
        deleteEmployeeById = new DeleteEmployeeById();
        ReflectionTestUtils.setField(deleteEmployeeById,"webClientBuilder",
                WebClient.builder().baseUrl("http://localhost:" + mockServer.getLocalPort()));
    }

    @Test
    void whenDeleteEmployeeByIdApi_IfGivesSuccess_thenReturnListOfEmployees() {
        mockServer.when(
                request()
                        .withMethod(HttpMethod.DELETE.name())
                        .withPath("/api/v1/delete/1")
        ).respond(
                response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(ChallengeAppTestHelper.deleteEmployeeByIdResponseFromApi())
        );

        mockServer.when(
                request()
                        .withMethod(HttpMethod.GET.name())
                        .withPath("/api/v1/employee/1")
        ).respond(
                response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(ChallengeAppTestHelper.getAllEmployeeByIdFromApi())
        );

        String status = deleteEmployeeById.execute("1");

        assertThat(status).isEqualTo(ChallengeAppTestConstants.SUCCESS);

        mockServer.verify(
                request().withMethod(HttpMethod.DELETE.name())
                        .withPath("/api/v1/delete/1")
        );

    }

    @Test
    void whenDeleteEmployeeByIdApi_IfGivesInternalServerErrorResponse_thenReturnInternalServerErrorResponse() {
        mockServer.when(
                request()
                        .withMethod(HttpMethod.DELETE.name())
                        .withPath("/api/v1/delete/1")
        ).respond(
                response()
                        .withStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        );

        Assertions.assertThrows(InternalServerError.class, ()-> deleteEmployeeById.execute("1"));

        mockServer.verify(
                request().withMethod(HttpMethod.DELETE.name())
                        .withPath("/api/v1/delete/1")
        );
    }

    @Test
    void whenDeleteEmployeeByIdApi_IfGivesTooManyRequestErrorResponse_thenReturnTooManyRequestErrorResponse() {
        mockServer.when(
                request()
                        .withMethod(HttpMethod.DELETE.name())
                        .withPath("/api/v1/delete/1")
        ).respond(
                response()
                        .withStatusCode(HttpStatus.TOO_MANY_REQUESTS.value())
        );

        Assertions.assertThrows(TooManyRequestException.class, ()-> deleteEmployeeById.execute("1"));

        mockServer.verify(
                request().withMethod(HttpMethod.DELETE.name())
                        .withPath("/api/v1/delete/1")
        );
    }

    @AfterEach
    public void tearDownServer() {
        mockServer.stop();
    }

}
