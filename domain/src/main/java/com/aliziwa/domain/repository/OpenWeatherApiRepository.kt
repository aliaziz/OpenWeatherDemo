package com.aliziwa.domain.repository

import com.aliziwa.domain.model.Weather
import com.aliziwa.domain.model.WeatherByCityNameRequest

/**
 * A contract between the business logic and the data layer
 */
interface OpenWeatherApiRepository {
    /**
     * Get weather by city name
     *
     * @param - request [WeatherByCityNameRequest]
     * Country code defaults to US. Can be configured in settings with a few UI Changes.
     * @return - [Result<Weather>]
     */
    suspend fun getWeatherByCityName(request: WeatherByCityNameRequest): Result<Weather>

    /**
     * Get weather by lat and long
     *
     * @param lat - [Double]
     * @param lon - [Double]
     * @return [Result<Weather>]
     */
    suspend fun getWeatherByGeoCords(lat: Double, lon: Double): Result<Weather>
}