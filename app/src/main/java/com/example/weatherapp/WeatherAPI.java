package com.example.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {
    @GET("weather?appid=2e4d7963b8476a920e28709a09323472&units=metric")
    Call<OpenWeatherMap> getWeatherWithLocation(
            @Query("lat") double lat,
            @Query("lon") double lon
    );
    @GET("weather?appid=2e4d7963b8476a920e28709a09323472&units=metric")
    Call<OpenWeatherMap> getWeatherWithName(@Query("q") String name);
}
