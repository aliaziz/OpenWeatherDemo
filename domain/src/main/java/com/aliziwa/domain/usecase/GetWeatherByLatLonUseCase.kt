package com.aliziwa.domain.usecase

import com.aliziwa.domain.Completed
import com.aliziwa.domain.failure
import com.aliziwa.domain.model.Weather
import com.aliziwa.domain.model.WeatherByLatLongRequest
import com.aliziwa.domain.repository.OpenWeatherApiRepository
import com.aliziwa.domain.success
import com.aliziwa.domain.usecase.base.FlowUseCase
import javax.inject.Inject

/**
 * Use case for getting current weather based on user's location
 */
abstract class GetWeatherByLatLonUseCase : FlowUseCase<WeatherByLatLongRequest, Weather>()

/**
 * [GetWeatherByLatLonUseCaseImpl] implements [GetWeatherByLatLonUseCase]
 *
 * @param openWeatherRepo - [OpenWeatherApiRepository] repository
 */
class GetWeatherByLatLonUseCaseImpl @Inject constructor(
    private val openWeatherRepo: OpenWeatherApiRepository
) : GetWeatherByLatLonUseCase() {
    override suspend fun executeInternal(data: WeatherByLatLongRequest): Completed<Weather> {
        return openWeatherRepo.getWeatherByGeoCords(data.lat, data.lon).fold(
            onSuccess = { success(it) },
            onFailure = { failure(it) }
        )
    }
}