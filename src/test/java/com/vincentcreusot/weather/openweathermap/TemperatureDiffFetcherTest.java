package com.vincentcreusot.weather.openweathermap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class TemperatureDiffFetcherTest {

    @Test
    void standardPostRequest() {
        String jsonStr = TemperatureDiffFetcher.preparePostRequest(5,10);
        assertEquals("{\"temperatureEnd\":\"10\",\"temperatureDiff\":\"5\",\"temperatureBegin\":\"5\"}", jsonStr);
    }
}
