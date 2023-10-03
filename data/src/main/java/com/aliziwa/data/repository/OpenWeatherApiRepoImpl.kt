package com.aliziwa.data.repository

import com.aliziwa.data.model.ForecastResponse
import com.aliziwa.data.model.toDomainForecast
import com.aliziwa.data.model.toDomainWeather
import com.aliziwa.data.repository.local.storage.WeatherStorage
import com.aliziwa.data.repository.remote.RetrofitOpenWeatherDataService
import com.aliziwa.data.repository.remote.RetrofitOpenWeatherGeoService
import com.aliziwa.domain.Failure
import com.aliziwa.domain.RemoteDataFailure
import com.aliziwa.domain.model.Weather
import com.aliziwa.domain.model.WeatherByCityNameRequest
import com.aliziwa.domain.repository.OpenWeatherApiRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.singleOrNull
import javax.inject.Inject

/**
 * Implementation of the [OpenWeatherApiRepository] contract.
 *
 * Duplicating request handling logic because of time. I could extract these into one function call later.
 *
 * @param retrofitDataService
 * @param retrofitGeoService
 * @param localStorage
 */
class OpenWeatherApiRepoImpl @Inject constructor(
    private val retrofitDataService: RetrofitOpenWeatherDataService,
    private val retrofitGeoService: RetrofitOpenWeatherGeoService,
    private val localStorage: WeatherStorage<Weather>
) : OpenWeatherApiRepository {

    /**
     * Gets the weather by city name.
     *
     * Behind the scenes, this method makes two API calls.
     * 1. To get the lat and long of that city's name. We take the first result in the least for simplicity
     * 2. To get the weather by the returned lat and long by calling [getWeatherByGeoCords]
     */
    override suspend fun getWeatherByCityName(request: WeatherByCityNameRequest): Result<Weather> {
        // Verify that the city name is not empty.
        return coroutineScope {
            // Get the city's lat and lon.
            async {
                val response = retrofitGeoService.getLatLonByCityName(request.toConcatFormat())
                if (!response.isSuccessful) return@async Result.failure(
                    Failure(
                        errorData = response.errorBody()?.string()
                    )
                )
                // If lat and long are not empty or no error occurred, proceed to get weather
                // by the returned lat and long

                // Note: I did not use Flow's .zip of .flatMapConcat because I opted to have one place to `collect`
                // the [Flow] from only the domain and UI layer. That made my testing easier.
                val cityLatLong = response.body()?.firstOrNull()
                cityLatLong?.let { r ->
                    getWeatherByGeoCords(lat = r.lat, lon = r.lon).also { result ->
                        result.onSuccess {
                            // Save weather searched to local cache.
                            localStorage.save(it).singleOrNull()
                        }
                    }
                } ?: kotlin.run {
                    Result.failure(RemoteDataFailure.EmptyResponseFailure)
                }
            }.await()
        }
    }

    /**
     * Gets the weather by [lat] and [lon]
     *
     * @param lat
     * @param lon
     *
     * @return [Result<Weather>]
     */
    override suspend fun getWeatherByGeoCords(lat: Double, lon: Double): Result<Weather> {
        return coroutineScope {
            async {
                // Get future forecast for the next 5 days
                val forecastResponse = async {
                    getFiveDayForecast(lat, lon)
                }.await()

                var forecastBody: ForecastResponse? = null
                if (forecastResponse.isSuccessful) forecastBody = forecastResponse.body()

                // Get current weather
                val currentWeatherResponse = async {
                    retrofitDataService.getWeather(lat, lon)
                }.await()
                if (!currentWeatherResponse.isSuccessful) return@async Result.failure(
                    Failure(
                        errorData = currentWeatherResponse.errorBody()?.string()
                    )
                )

                val currentWeatherBody = currentWeatherResponse.body()

                //Checking only the current weather. Forecast failures should not break entire api call
                currentWeatherBody?.let { currentWeather ->

                    // Concatenate the forecast with the current weather

                    // Note: I did not use Flow's .zip of .flatMapConcat because I opted to have one place to `collect`
                    // the [Flow] from only the domain and UI layer. That made my testing easier.
                    val updatedWeather = currentWeather.toDomainWeather().copy(
                        forecastWeather = forecastBody?.list?.map { it.toDomainForecast() }
                            ?: emptyList()
                    )
                    return@async Result.success(updatedWeather)
                } ?: kotlin.run {
                    return@async Result.failure(RemoteDataFailure.EmptyResponseFailure)
                }
            }.await()
        }
    }

    private suspend fun getFiveDayForecast(lat: Double, lon: Double) =
        retrofitDataService.getFiveDayForecast(lat, lon)
}

