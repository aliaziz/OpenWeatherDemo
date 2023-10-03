package com.aliziwa.domain.usecase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aliziwa.domain.LocalDataFailure
import com.aliziwa.domain.model.DummyData.dummyCurrentWeather
import com.aliziwa.domain.model.Weather
import com.aliziwa.domain.repository.OpenWeatherDataRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetLastSearchUseCaseImplTest {
    private lateinit var getLastSearchUseCase: GetLastSearchUseCase
    private val openWeatherDataRepository: OpenWeatherDataRepository = mockk(relaxed = true)

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        getLastSearchUseCase = GetLastSearchUseCaseImpl(openWeatherDataRepository)
    }

    @Test
    fun `test invoking usecase on success returns weather`() = runTest {
        //Given
        coEvery { openWeatherDataRepository.getLastSearchCityWeather() } returns flow {
            emit(
                Weather(
                    currentWeather = dummyCurrentWeather(),
                    forecastWeather = emptyList()
                )
            )
        }

        //When
        val response = getLastSearchUseCase(Unit).singleOrNull()

        //Then
        assertThat(response).isNotNull()
    }

    /**
     * Verifying that the flow will throw an exception.
     */
    @Test(expected = LocalDataFailure.NoCachedDataFailure::class)
    fun `test invoking usecase on failure returns error`() = runTest {
        //Given
        coEvery { openWeatherDataRepository.getLastSearchCityWeather() } returns flow {
            throw LocalDataFailure.NoCachedDataFailure
        }

        //When
        val response = getLastSearchUseCase(Unit).singleOrNull()

        //Then
        assertThat(response).isNotNull()
    }
}