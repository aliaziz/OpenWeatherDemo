package com.aliziwa.openweatherjpmc.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aliziwa.domain.model.DummyData.dummyCurrentWeather
import com.aliziwa.domain.model.Weather
import com.aliziwa.domain.success
import com.aliziwa.domain.usecase.GetLastSearchUseCase
import com.aliziwa.domain.usecase.GetWeatherByCityNameUseCase
import com.aliziwa.domain.usecase.GetWeatherByLatLonUseCase
import com.aliziwa.openweatherjpmc.ui.ext.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    private val cityNameUseCase: GetWeatherByCityNameUseCase =
        mockk<GetWeatherByCityNameUseCase>(relaxed = true).apply {
            every { execute(any()) } returns flow {
                emit(
                    success(
                        Weather(
                            currentWeather = dummyCurrentWeather(),
                            forecastWeather = emptyList()
                        )
                    )
                )
            }
        }
    private val latLonUseCase: GetWeatherByLatLonUseCase =
        mockk<GetWeatherByLatLonUseCase>(relaxed = true).apply {
            every { execute(any()) } returns flow {
                emit(
                    success(
                        Weather(
                            currentWeather = dummyCurrentWeather(),
                            forecastWeather = emptyList()
                        )
                    )
                )
            }
        }
    private val lastSearchUseCase: GetLastSearchUseCase = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mainViewModel: MainViewModel

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        mainViewModel = MainViewModel(
            cityNameUseCase,
            latLonUseCase,
            lastSearchUseCase,
            testDispatcher
        )
    }

    @Test
    fun `test getCurrentWeather returns weather data through flow`() = runTest(testDispatcher) {
        //When
        mainViewModel.getCurrentWeather(0.1, 0.2)
        advanceUntilIdle()
        val values = mainViewModel.weatherData.getOrAwaitValue()

        //Then
        with(values) {
            assertThat(this.isSuccess).isTrue()
            onSuccess {
                assertThat(it.currentWeather.location).isEqualTo(dummyCurrentWeather().location)
                /**
                 * We could assert all other attributes here. For time's sake i'll leave them out.
                 */
            }
            assertThat(isFailure).isFalse()
        }
    }

    @Test
    fun `test searchForCityWeather returns weather data through flow`() = runTest(testDispatcher) {
        //Given
        mainViewModel.ioScope = this

        //When
        mainViewModel.searchForCityWeather("New york", "NY")
        advanceUntilIdle()
        val values = mainViewModel.weatherData.getOrAwaitValue()

        //Then
        with(values) {
            assertThat(isSuccess).isTrue()
            onSuccess {
                assertThat(it.currentWeather.location).isEqualTo(dummyCurrentWeather().location)
                /**
                 * We could assert all other attributes here. For time's sake i'll leave them out.
                 */
            }
            assertThat(isFailure).isFalse()
        }

    }

    @Test
    fun `test getLastSearchedWeather returns last saved search data`() = runTest(testDispatcher) {
        //Given
        mainViewModel.ioScope = this
        coEvery {
            lastSearchUseCase(Unit)
        } returns flow {
            emit(
                Weather(
                    currentWeather = dummyCurrentWeather(),
                    forecastWeather = emptyList()
                )
            )
        }

        //When
        mainViewModel.getLastSearchedWeather()
        advanceUntilIdle()
        val values = mainViewModel.weatherData.getOrAwaitValue()

        //Then
        with(values) {
            assertThat(isSuccess).isTrue()
            onSuccess {
                assertThat(it.currentWeather.location).isEqualTo(dummyCurrentWeather().location)
                /**
                 * We could assert all other attributes here. For time's sake i'll leave them out.
                 */
            }
            assertThat(isFailure).isFalse()
        }
    }
}
