package com.aliziwa.domain.usecase

import com.aliziwa.domain.LocalDataFailure
import com.aliziwa.domain.model.Weather
import com.aliziwa.domain.repository.OpenWeatherDataRepository
import com.aliziwa.domain.usecase.base.UseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

abstract class GetLastSearchUseCase : UseCase<Unit, Weather>()

/**
 * Get last searched weather.
 */
class GetLastSearchUseCaseImpl @Inject constructor(
    private val openWeatherDataRepo: OpenWeatherDataRepository,
) : GetLastSearchUseCase() {

    override suspend fun invoke(input: Unit): Flow<Weather> {
        return flow {
            /**
             * Handling case of missing data instead of doing so in the ViewModel
             */
            openWeatherDataRepo.getLastSearchCityWeather().collect { weather ->
                if (weather == null) throw LocalDataFailure.NoCachedDataFailure
                emit(weather)
            }
        }
    }
}