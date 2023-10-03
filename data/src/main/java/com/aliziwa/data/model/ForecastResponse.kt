package com.aliziwa.data.model

import com.aliziwa.domain.model.ForeCastWeather
import com.aliziwa.domain.model.WeatherDetails
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.Locale


@JsonClass(generateAdapter = true)
data class ForecastResponse(
    val cod: String? = null,
    val message: Long? = null,
    val cnt: Long? = null,
    val list: List<ListElement>? = null,
    val city: City? = null
)

@JsonClass(generateAdapter = true)
data class City(
    val id: Long? = null,
    val name: String? = null,
    val coord: Coord? = null,
    val country: String? = null,
    val population: Long? = null,
    val timezone: Long? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null
)

@JsonClass(generateAdapter = true)
data class ListElement(
    val dt: Long? = null,
    val main: Main? = null,
    val weather: List<Weather>? = null,
    val clouds: Clouds? = null,
    val wind: Wind? = null,
    val visibility: Long? = null,
    val pop: Double? = null,
    val sys: Sys? = null,

    @Json(name = "dt_txt")
    val dtTxt: String? = null
)

@JsonClass(generateAdapter = true)
data class Main(
    val temp: Double? = null,

    @Json(name = "feels_like")
    val feelsLike: Double? = null,

    @Json(name = "temp_min")
    val tempMin: Double? = null,

    @Json(name = "temp_max")
    val tempMax: Double? = null,

    val pressure: Long? = null,

    @Json(name = "sea_level")
    val seaLevel: Long? = null,

    @Json(name = "grnd_level")
    val grndLevel: Long? = null,

    val humidity: Long? = null,

    @Json(name = "temp_kf")
    val tempKf: Double? = null
)

@JsonClass(generateAdapter = true)
data class Wind(
    val speed: Double? = null,
    val deg: Long? = null,
    val gust: Double? = null
)

fun ListElement.toDomainForecast() = ForeCastWeather(
    weekDay = SimpleDateFormat("EEE", Locale.ENGLISH).format(dt?.or(0)),
    details = WeatherDetails(
        highTemp = main?.tempMax?.toString().orEmpty(),
        lowTemp = main?.tempMin?.toString().orEmpty(),
        currentTemp = main?.temp?.toString().orEmpty(),
        icon = weather?.firstOrNull()?.icon.orEmpty()
    )
)
