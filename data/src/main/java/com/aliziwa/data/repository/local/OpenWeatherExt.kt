package com.aliziwa.data.repository.local

import com.aliziwa.data.LocalCurrentWeather
import com.aliziwa.data.LocalForeCastWeather
import com.aliziwa.data.LocalWeatherDetails
import com.aliziwa.domain.model.CurrentWeather
import com.aliziwa.domain.model.ForeCastWeather
import com.aliziwa.domain.model.WeatherDetails

/**
 * This File contains mappers from the domain Weather object to the Cached Weather object
 */

fun CurrentWeather.toLocalCurrentWeather(): LocalCurrentWeather {
    return LocalCurrentWeather.newBuilder()
        .setLocation(location)
        .setDetails(details.toLocalWeatherDetails())
        .setDescription(description)
        .build()
}

fun WeatherDetails.toLocalWeatherDetails(): LocalWeatherDetails {
    return LocalWeatherDetails.newBuilder()
        .setCurrentTemp(currentTemp)
        .setHighTemp(highTemp)
        .setLowTemp(lowTemp)
        .setIcon(icon)
        .build()
}

fun LocalCurrentWeather.toCurrentWeather() : CurrentWeather {
    return CurrentWeather(
        location = location,
        details = details.toWeatherDetails(),
        description = description
    )
}

fun LocalWeatherDetails.toWeatherDetails() : WeatherDetails {
    return WeatherDetails(
        highTemp = highTemp,
        lowTemp = lowTemp,
        currentTemp = currentTemp,
        icon = icon
    )
}

fun LocalForeCastWeather.toForecastWeather(): ForeCastWeather {
    return ForeCastWeather(
        weekDay = weekDay,
        details = details.toWeatherDetails()
    )
}
