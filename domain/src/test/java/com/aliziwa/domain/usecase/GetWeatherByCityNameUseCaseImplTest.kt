package com.aliziwa.domain.usecase

import com.aliziwa.domain.AsyncOperation
import com.aliziwa.domain.Completed
import com.aliziwa.domain.Loading
import com.aliziwa.domain.RemoteDataFailure
import com.aliziwa.domain.model.DummyData.dummyCurrentWeather
import com.aliziwa.domain.model.Weather
import com.aliziwa.domain.model.WeatherByCityNameRequest
import com.aliziwa.domain.repository.OpenWeatherApiRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetWeatherByCityNameUseCaseImplTest {
    private lateinit var getWeatherByCityNameUseCase: GetWeatherByCityNameUseCase
    private val openWeatherApiRepository: OpenWeatherApiRepository = mockk(relaxed = true)

    @Before
    fun setup() {
        getWeatherByCityNameUseCase = GetWeatherByCityNameUseCaseImpl(openWeatherApiRepository)
    }

    @Test
    fun `test usecase on success returns weather data`() = runTest {
        //Given
        coEvery { openWeatherApiRepository.getWeatherByCityName(any()) } returns Result.success(
            Weather(
                currentWeather = dummyCurrentWeather(),
                forecastWeather = emptyList()
            )
        )
        val request = WeatherByCityNameRequest(
            cityName = "Brandon",
            state = "FL"
        )

        //When
        val values = mutableListOf<AsyncOperation<Weather>>()
        getWeatherByCityNameUseCase.execute(request).toList(values)

        //Then
        assertThat(values).isNotEmpty()
        with(values) {
            /**
             * Verifying that the first request was a [Loading] operation
             * ...
             * then a [Completed] operation
             */
            assertThat(first()::class).isEqualTo(Loading::class)
            assertThat(last()::class).isEqualTo(Completed::class)
            assertThat(last().isSuccess).isTrue()
        }
    }

    @Test
    fun `test usecase on failure returns failure data`() = runTest {
        //Given
        coEvery { openWeatherApiRepository.getWeatherByCityName(any()) } returns Result.failure(RemoteDataFailure.RequestFailure)
        val request = WeatherByCityNameRequest(
            cityName = "Brandon",
            state = "FL"
        )

        //When
        val values = mutableListOf<AsyncOperation<Weather>>()
        getWeatherByCityNameUseCase.execute(request).toList(values)

        //Then
        assertThat(values).isNotEmpty()
        with(values) {
            /**
             * Verifying that the first request was a [Loading] operation
             * ...
             * then a [Completed] operation
             */
            assertThat(first()::class).isEqualTo(Loading::class)
            assertThat(last()::class).isEqualTo(Completed::class)
            assertThat(last().isFailure).isTrue()
        }
    }
}