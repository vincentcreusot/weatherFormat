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
        Properties appProps = loadProperties();

        String webApiUrl = appProps.getProperty("weather.timemachine.url");
        String postUrl = appProps.getProperty("post.url");
        String lat = appProps.getProperty("position.lat");
        String lon = appProps.getProperty("position.lon");
        logger.info("Hello weather!");
        logger.info("Loading properties");

        Dates datesUsed = formatDates(appProps);
        String key = appProps.getProperty("weather.key");

        TemperatureDiffFetcher fetcher = new TemperatureDiffFetcher(webApiUrl, postUrl, key);
        int returned = fetcher.fetchAndPush(datesUsed.dateBegin(), datesUsed.dateEnd(), lat, lon);
        logger.info("Returned " + returned);
    }

    private static Dates formatDates(Properties appProps) {
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
        return new Dates(dateBegin, dateEnd);
    }

    private record Dates(String dateBegin, String dateEnd) { }

    private static Properties loadProperties() {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "app.properties";
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return appProps;
    }


}