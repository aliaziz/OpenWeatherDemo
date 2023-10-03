package com.aliziwa.domain.repository

import com.aliziwa.domain.model.Weather
import kotlinx.coroutines.flow.Flow

/**
 * [OpenWeatherDataRepository] - Contains methods to communicate with the [Storage] api
 */
interface OpenWeatherDataRepository {
    /**
     * Get the last searched city's weather.
     *
     * @return [Flow<Weather>]
     */
    suspend fun getLastSearchCityWeather(): Flow<Weather?>
}