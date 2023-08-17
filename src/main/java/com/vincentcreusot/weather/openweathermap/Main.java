package com.vincentcreusot.weather.openweathermap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "app.properties";
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String postUrl = appProps.getProperty("post.url");
        String lat = appProps.getProperty("position.lat");
        String lon = appProps.getProperty("position.lon");
        logger.info("Hello weather!");
        logger.info("Loading properties");

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        Date dtBegin, dtEnd;
        try {
            dtBegin = df.parse(appProps.getProperty("date.begin"));
            dtEnd = df.parse(appProps.getProperty("date.end"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String dateBegin = Long.toString(dtBegin.getTime() / 1000);
        String dateEnd = Long.toString(dtEnd.getTime() / 1000);
        String key = appProps.getProperty("weather.key");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = getHttpRequest(appProps, lat, lon, dateBegin, key);
        HttpRequest requestEnd = getHttpRequest(appProps, lat, lon, dateEnd, key);
        try {
            TimeMachineResponse bodyBegin = getTemperature(client, request);
            TimeMachineResponse bodyEnd = getTemperature(client, requestEnd);
            HttpRequest postRequest = HttpRequest.newBuilder(URI.create(postUrl))
                    .POST(HttpRequest.BodyPublishers.ofString(preparePostRequest(
                            bodyBegin.getData().get(0).getTemp(),
                            bodyEnd.getData().get(0).getTemp()
                    )))
                    .setHeader("Content-Type", "application/json")
                    .build();

            HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

            logger.info("Status code " + postResponse.statusCode());
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

    private static HttpRequest getHttpRequest(Properties appProps, String lat, String lon, String dateBegin, String key) {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(appProps.getProperty("weather.timemachine.url") + prepareGetRequest(lat, lon, dateBegin, key)))
                .GET()
                .header("Accept", "application/json")
                .build();
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