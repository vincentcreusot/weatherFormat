package com.vincentcreusot.weather.openweathermap;

import java.util.List;

public class TimeMachineResponse {

   private List<TimeMachineResponseData> data;
   private long lat;
   private long lon;

   private String timezone;
   private long timezone_offset;

   public List<TimeMachineResponseData> getData() {
      return data;
   }

   public void setData(List<TimeMachineResponseData> data) {
      this.data = data;
   }

   public long getLat() {
      return lat;
   }

   public void setLat(long lat) {
      this.lat = lat;
   }

   public long getLon() {
      return lon;
   }

   public void setLon(long lon) {
      this.lon = lon;
   }

   public String getTimezone() {
      return timezone;
   }

   public void setTimezone(String timezone) {
      this.timezone = timezone;
   }

   public long getTimezone_offset() {
      return timezone_offset;
   }

   public void setTimezone_offset(long timezone_offset) {
      this.timezone_offset = timezone_offset;
   }
}
