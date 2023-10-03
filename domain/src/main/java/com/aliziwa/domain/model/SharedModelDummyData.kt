package com.aliziwa.domain.model

object DummyData {
    fun dummyCurrentWeather() = CurrentWeather(
        location = "Los Angeles",
        description = "Warm",
        details = WeatherDetails(
            highTemp = "79.2",
            lowTemp = "77.9",
            currentTemp = "78.1",
            icon = "10n"
        )
    )
}