package com.aliziwa.data.repository.remote

import com.aliziwa.data.model.ForecastResponse
import com.aliziwa.data.model.WeatherDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * A retrofit api service to capture `Weather Data` api calls from OpenWeatherApi.
 *
 * I'm choosing to splitting these two services because retrofit doesn't support runtime switching of the base url.
 * This approach seemed simpler for me.
 */
interface RetrofitOpenWeatherDataService {

    /**
     * Gets weather forecast for the current location.
     *
     * @param lat [Long]
     * @param lon [Long]
     */
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<WeatherDataResponse>

    /**
     * Gets weather forecast for the next @param count days from today's date.
     *
     * @param lat [Long]
     * @param lon [Long]
     * @param count [Int] I'm choosing to get the next 5 days.
     */
    @GET("forecast")
    suspend fun getFiveDayForecast(
        @Query("lat") lon: Double,
        @Query("lon") lat: Double,
        @Query("cnt") count: String = "5"
    ): Response<ForecastResponse>

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    }
}