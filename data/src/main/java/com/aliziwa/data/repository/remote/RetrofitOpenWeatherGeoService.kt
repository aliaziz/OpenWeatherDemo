package com.aliziwa.data.repository.remote

import com.aliziwa.data.model.DirectGeoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * A retrofit api service to capture `Geocoding api calls from OpenWeatherApi.
 */
interface RetrofitOpenWeatherGeoService {
    /**
     * Using the ReverseGeoCoding API, captures the lat long for a given city name.
     * Assumes, country as US - This can be changed in the settings screen in the app.
     * Invalid country codes will default to US too.
     *
     * @param query [String] formatted as [`cityName, State, Country code`]
     * @param limit [String] limits the number of cities with the same name to 3 by default
     *
     * @return [DirectGeoResponse]
     */
    @GET("direct")
    suspend fun getLatLonByCityName(
        @Query("q") query: String,
        @Query("limit") limit: String = "3"
    ): Response<DirectGeoResponse>

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/geo/1.0/"
    }
}