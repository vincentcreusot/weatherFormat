package com.vincentcreusot.weather.openweathermap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class TemperatureDiffFetcherIntegrationTest {
    private static ClientAndServer mockServer;

    @BeforeAll
    public static void startServer() {
        mockServer = startClientAndServer(1080);
    }

    @Test
    public void whenGetSameTemperature() {
        createExpectationForATemperature();
        TemperatureDiffFetcher f = new TemperatureDiffFetcher("http://localhost:1080/timemachine", "http://localhost:1080/post", "key");
        int r = f.fetchAndPush("1234", "1234", "20", "-20");
        assertEquals(200, r);
    }

    private void createExpectationForATemperature() {
        var s = new MockServerClient("127.0.0.1", 1080);
        s.when(request()
                                .withMethod("GET")
                                .withPath("/timemachine*")
                                .withHeaders(
                                        new Header("Accept", "application/json")
                                ),
                        exactly(2)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(
                                        new Header("Content-Type", "application/json")
                                )
                                .withBody("{\"lat\":33.44,\"lon\":-94.04,\"timezone\":\"America/Chicago\",\"timezone_offset\":-18000,\"data\":[{\"dt\":484207200,\"temp\":18.03,\"pressure\":1016,\"humidity\":70}]}")
                );
        s.when(
                        request()
                                .withMethod("POST")
                                .withPath("/post"),
                        exactly(1)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(
                                        new Header("Content-Type", "application/json")
                                )
                                .withBody("{message:ok}")
        );
    }

    @AfterAll
    public static void stopServer() {
        mockServer.stop();
    }

}
