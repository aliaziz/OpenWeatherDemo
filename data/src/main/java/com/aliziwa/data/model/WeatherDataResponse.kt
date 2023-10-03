package com.aliziwa.data.model

import com.aliziwa.domain.model.CurrentWeather
import com.aliziwa.domain.model.WeatherDetails
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherDataResponse(
    val coord: Coord? = null,
    val weather: List<Weather>? = null,
    val base: String? = null,
    val main: Main? = null,
    val visibility: Long? = null,
    val wind: Wind? = null,
    val clouds: Clouds? = null,
    val dt: Long? = null,
    val sys: Sys? = null,
    val timezone: Long? = null,
    val id: Long? = null,
    val name: String? = null,
    val cod: Long? = null
)

@JsonClass(generateAdapter = true)
data class Clouds(
    val all: Long? = null
)

@JsonClass(generateAdapter = true)
data class Coord(
    val lon: Double? = null,
    val lat: Double? = null
)

@JsonClass(generateAdapter = true)
data class Sys(
    val type: Long? = null,
    val id: Long? = null,
    val pod: String? = null,
    val country: String? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null
)

@JsonClass(generateAdapter = true)
data class Weather(
    val id: Long? = null,
    val main: String? = null,
    val description: String? = null,
    val icon: String? = null
)

fun WeatherDataResponse.toDomainWeather() = com.aliziwa.domain.model.Weather(
    currentWeather = CurrentWeather(
        name.orEmpty(),
        description = weather?.firstOrNull()?.description + " feels like "+ main?.feelsLike,
        details = WeatherDetails(
            highTemp = main?.tempMax?.toString().orEmpty(),
            lowTemp = main?.tempMin?.toString().orEmpty(),
            currentTemp = main?.temp?.toString().orEmpty(),
            icon = weather?.firstOrNull()?.icon.orEmpty()
        )
    ),
    forecastWeather = emptyList()
)