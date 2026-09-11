package com.dreammania.homecrew.weather

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

class WeatherViewModel : ViewModel() {

    var weatherText by mutableStateOf("Weer wordt geladen...")
        private set

    var forecastItems by mutableStateOf<List<ForecastItem>>(emptyList())
        private set

    var sunrise by mutableLongStateOf(0L)
        private set

    var sunset by mutableLongStateOf(0L)
        private set

    private val weatherService: WeatherService

    init {
        val json = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        weatherService = retrofit.create(WeatherService::class.java)
    }

    private fun mutableLongStateOf(value: Long) = mutableStateOf(value)

    fun fetchWeather(location: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val weatherResponse = weatherService.getCurrentWeather(location, apiKey)
                val temp = weatherResponse.main.temp.toInt()
                val description = weatherResponse.weather.firstOrNull()?.description ?: ""
                weatherText = "Het is momenteel $description, $temp°C."
                
                val forecastResponse = weatherService.getFiveDayForecast(location, apiKey)
                sunrise = forecastResponse.city?.sunrise ?: 0L
                sunset = forecastResponse.city?.sunset ?: 0L

                // OpenWeatherMap returns data every 3 hours. Let's pick one per day (e.g., around 12:00)
                forecastItems = forecastResponse.list.filter { it.dtTxt.contains("12:00:00") }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather", e)
                weatherText = "Kon het weer niet ophalen."
            }
        }
    }
}
