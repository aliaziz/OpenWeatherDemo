package com.aliziwa.domain.model

data class Weather(
    val currentWeather: CurrentWeather,
    val forecastWeather: List<ForeCastWeather>
)

data class CurrentWeather(
    val location: String,
    val description: String? = null,
    val details: WeatherDetails
)

data class ForeCastWeather(
    val weekDay: String,
    val details: WeatherDetails
)

data class WeatherDetails(
    val highTemp: String,
    val lowTemp: String,
    val currentTemp: String,
    val icon: String
)
