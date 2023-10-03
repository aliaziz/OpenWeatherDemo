package com.aliziwa.data.repository

import com.aliziwa.data.model.DirectGeoResponseElement
import com.aliziwa.data.model.ForecastResponse
import com.aliziwa.data.model.WeatherDataResponse
import com.aliziwa.data.repository.remote.RetrofitOpenWeatherDataService
import com.aliziwa.data.repository.remote.RetrofitOpenWeatherGeoService
import com.aliziwa.domain.model.WeatherByCityNameRequest
import com.aliziwa.domain.repository.OpenWeatherApiRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Test functionality of [OpenWeatherApiRepository]
 */
class OpenWeatherApiRepoImplTest {

    private lateinit var openWeatherApiRepository: OpenWeatherApiRepository
    private val dataService: RetrofitOpenWeatherDataService =
        mockk<RetrofitOpenWeatherDataService>(relaxed = true).apply {
            /**
             * Mocking response from weather api call
             */
            coEvery { getWeather(any(), any()) } returns Response.success(WeatherDataResponse())

            /**
             * Mocking response from forecast api call
             */
            coEvery { getFiveDayForecast(any(), any(), any()) } returns Response.success(
                ForecastResponse()
            )
        }

    private val geoService: RetrofitOpenWeatherGeoService =
        mockk<RetrofitOpenWeatherGeoService>(relaxed = true).apply {
            /**
             * Mocking response from latlong api call
             */
            coEvery { getLatLonByCityName(any(), any()) } returns Response.success(
                listOf(
                    DirectGeoResponseElement()
                )
            )
        }
    private val testScheduler = TestCoroutineScheduler()

    /**
     * Using unconfined here because I have another nested coroutine in the api calls.
     */
    private val unconfinedDispatcher = UnconfinedTestDispatcher(testScheduler)

    @Before
    fun setup() {
        openWeatherApiRepository = OpenWeatherApiRepoImpl(
            dataService,
            geoService,
            localStorage
        )
    }

    @Test
    fun `test getWeatherByCityName invokes retrofitGeoService and retrofitDataGeoService`() = runTest(unconfinedDispatcher) {
        //Given
        val request = WeatherByCityNameRequest("Brandon", "FL")

        //When
        val weatherByCityName = openWeatherApiRepository.getWeatherByCityName(request)

        //Then
        coVerify(exactly = 1) { geoService.getLatLonByCityName(any(), any()) }
        coVerify(exactly = 1) { dataService.getWeather(any(), any()) }
        assertThat(weatherByCityName.isSuccess).isTrue()
    }

    @Test
    fun `test getWeatherByGeoCords invokes retrofitDataGeoService only`() = runTest(unconfinedDispatcher) {
        //Given
        val lat = 0.0
        val lon = 0.0

        //When
        val weatherByCityName = openWeatherApiRepository.getWeatherByGeoCords(lat, lon)

        //Then
        coVerify(exactly = 0) { geoService.getLatLonByCityName(any(), any()) }
        coVerify(exactly = 1) { dataService.getWeather(any(), any()) }
        assertThat(weatherByCityName.isSuccess).isTrue()
    }

    @After
    fun clearup() {
        testScheduler.cancel()
    }
}