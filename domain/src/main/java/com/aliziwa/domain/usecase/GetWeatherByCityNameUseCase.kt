package com.aliziwa.domain.usecase

import com.aliziwa.domain.Completed
import com.aliziwa.domain.failure
import com.aliziwa.domain.model.Weather
import com.aliziwa.domain.model.WeatherByCityNameRequest
import com.aliziwa.domain.repository.OpenWeatherApiRepository
import com.aliziwa.domain.success
import com.aliziwa.domain.usecase.base.FlowUseCase
import javax.inject.Inject

/**
 * Get current weather by city name search.
 */
abstract class GetWeatherByCityNameUseCase : FlowUseCase<WeatherByCityNameRequest, Weather>()

class GetWeatherByCityNameUseCaseImpl @Inject constructor(
    private val openWeatherRepo: OpenWeatherApiRepository,
) : GetWeatherByCityNameUseCase() {
    override suspend fun executeInternal(data: WeatherByCityNameRequest): Completed<Weather> {
        return openWeatherRepo.getWeatherByCityName(data).fold(
            onSuccess = { success(it) },
            onFailure = { failure(it) }
        )
    }
}

