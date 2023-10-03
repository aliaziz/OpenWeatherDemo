package com.aliziwa.data.repository

import com.aliziwa.data.repository.local.storage.WeatherStorage
import com.aliziwa.domain.model.Weather
import com.aliziwa.domain.repository.OpenWeatherDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * [OpenWeatherDataRepoImpl] - Implementation of the [OpenWeatherDataRepository]
 */
class OpenWeatherDataRepoImpl @Inject constructor(
    private val localStorage: WeatherStorage<Weather>
) : OpenWeatherDataRepository {
    /**
     * Get the last searched and saved city
     *
     * @return - [Result]
     */
    override suspend fun getLastSearchCityWeather(): Flow<Weather?> {
        return localStorage.get()
    }
}