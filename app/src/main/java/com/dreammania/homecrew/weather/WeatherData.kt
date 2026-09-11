@file:Suppress("PLUGIN_IS_NOT_ENABLED")
package com.dreammania.homecrew.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("weather") val weather: List<Weather>,
    @SerialName("main") val main: Main,
    @SerialName("wind") val wind: Wind? = null,
    @SerialName("clouds") val clouds: Clouds? = null,
    @SerialName("sys") val sys: Sys? = null
)

@Serializable
data class ForecastResponse(
    @SerialName("list") val list: List<ForecastItem>,
    @SerialName("city") val city: City? = null
)

@Serializable
data class ForecastItem(
    @SerialName("dt") val dt: Long,
    @SerialName("main") val main: Main,
    @SerialName("weather") val weather: List<Weather>,
    @SerialName("clouds") val clouds: Clouds? = null,
    @SerialName("wind") val wind: Wind? = null,
    @SerialName("dt_txt") val dtTxt: String
)

@Serializable
data class Weather(
    @SerialName("description") val description: String,
    @SerialName("icon") val icon: String = ""
)

@Serializable
data class Main(
    @SerialName("temp") val temp: Double,
    @SerialName("temp_min") val tempMin: Double = 0.0,
    @SerialName("temp_max") val tempMax: Double = 0.0,
    @SerialName("humidity") val humidity: Int = 0
)

@Serializable
data class Wind(
    @SerialName("speed") val speed: Double
)

@Serializable
data class Clouds(
    @SerialName("all") val all: Int
)

@Serializable
data class Sys(
    @SerialName("sunrise") val sunrise: Long,
    @SerialName("sunset") val sunset: Long
)

@Serializable
data class City(
    @SerialName("sunrise") val sunrise: Long,
    @SerialName("sunset") val sunset: Long
)
