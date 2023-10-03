package com.aliziwa.domain.model

data class WeatherByCityNameRequest(
    val cityName: String,
    val state: String,
    val country: String = "US"
) {
    fun toConcatFormat() = "$cityName,$state,$country"
}

data class WeatherByLatLongRequest(
    val lat: Double,
    val lon: Double
)