package com.vincentcreusot.weather.openweathermap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.logging.Logger;

public class TemperatureDiffFetcher {
    private static final Logger logger = Logger.getLogger(TemperatureDiffFetcher.class.getName());
    private String webApiUrl;
    private String postApiUrl;
    private String webApiKey;

    public TemperatureDiffFetcher(String webApiUrl, String postApiUrl, String webApiKey) {
        this.webApiUrl = webApiUrl;
        this.postApiUrl = postApiUrl;
        this.webApiKey = webApiKey;
    }

    public int fetchAndPush(String timestampBegin, String timestampEnd, String lat, String lon) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = getHttpRequest(this.webApiUrl, lat, lon, timestampBegin, this.webApiKey);
        HttpRequest requestEnd = getHttpRequest(this.webApiUrl, lat, lon, timestampEnd, this.webApiKey);
        try {
            TimeMachineResponse bodyBegin = getTemperature(client, request);
            TimeMachineResponse bodyEnd = getTemperature(client, requestEnd);
            HttpRequest postRequest = HttpRequest.newBuilder(URI.create(this.postApiUrl)).POST(HttpRequest.BodyPublishers.ofString(preparePostRequest(bodyBegin.getData().get(0).getTemp(), bodyEnd.getData().get(0).getTemp()))).setHeader("Content-Type", "application/json").build();

            HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

            return postResponse.statusCode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static TimeMachineResponse getTemperature(HttpClient client, HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        logger.info("body " + response.body());
        ObjectMapper bodyMapper = new ObjectMapper();

        TimeMachineResponse o = bodyMapper.readValue(response.body(), TimeMachineResponse.class);
        return o;
    }

    private static HttpRequest getHttpRequest(String url, String lat, String lon, String dateBegin, String key) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url + prepareGetRequest(lat, lon, dateBegin, key))).GET().header("Accept", "application/json").build();
        return request;
    }

    public static String prepareGetRequest(String lat, String lon, String dt, String apiKey) {
        StringBuilder builder = new StringBuilder();
        builder = builder.append('?').append("lat=").append(lat).append("&lon=").append(lon).append("&dt=").append(dt).append("&appid=").append(apiKey).append("&units=metric");
        return builder.toString();
    }

    public static String preparePostRequest(long tempBegin, long tempEnd) {
        var values = new HashMap() {
            {
                put("temperatureBegin", Long.toString(tempBegin));
                put("temperatureEnd", Long.toString(tempEnd));
                put("temperatureDiff", Long.toString(tempEnd - tempBegin));
            }
        };

        var objectMapper = new ObjectMapper();
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
        return requestBody;
    }
}
