package com.vincentcreusot.weather.openweathermap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class TimeMachineResponseData {

    private long temp;
    private long dt;
    private long feels_like;
    private long pressure;
    private long humidity;
    private long dew_point;
    private long clouds;
    private long visibility;
    private long wind_speed;
    private long wind_deg;

    public long getTemp() {
        return temp;
    }

    public void setTemp(long temp) {
        this.temp = temp;
    }

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public long getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(long feels_like) {
        this.feels_like = feels_like;
    }

    public long getPressure() {
        return pressure;
    }

    public void setPressure(long pressure) {
        this.pressure = pressure;
    }

    public long getHumidity() {
        return humidity;
    }

    public void setHumidity(long humidity) {
        this.humidity = humidity;
    }

    public long getDew_point() {
        return dew_point;
    }

    public void setDew_point(long dew_point) {
        this.dew_point = dew_point;
    }

    public long getClouds() {
        return clouds;
    }

    public void setClouds(long clouds) {
        this.clouds = clouds;
    }

    public long getVisibility() {
        return visibility;
    }

    public void setVisibility(long visibility) {
        this.visibility = visibility;
    }

    public long getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(long wind_speed) {
        this.wind_speed = wind_speed;
    }

    public long getWind_deg() {
        return wind_deg;
    }

    public void setWind_deg(long wind_deg) {
        this.wind_deg = wind_deg;
    }
}
