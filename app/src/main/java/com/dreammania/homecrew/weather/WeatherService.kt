package com.dreammania.homecrew.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") location: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "nl"
    ): WeatherResponse

    @GET("data/2.5/forecast")
    suspend fun getFiveDayForecast(
        @Query("q") location: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "nl"
    ): ForecastResponse
}
