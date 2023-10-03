package com.aliziwa.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aliziwa.domain.model.DummyData.dummyCurrentWeather
import com.aliziwa.domain.model.Weather
import com.aliziwa.domain.repository.OpenWeatherDataRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Testing that repository saves and gets data
 */
class OpenWeatherDataRepoImplTest {
    private lateinit var dataRepository: OpenWeatherDataRepository

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        dataRepository = OpenWeatherDataRepoImpl(localStorage)
    }

    @Test
    fun `test getLastSearchCityWeather on empty data returns nothing`() = runTest {
        val result = dataRepository.getLastSearchCityWeather().singleOrNull()
        assertThat(result).isNull()
    }

    @Test
    fun `test getLastSearchCityWeather with saved data returns last saved`() = runTest {
        //Given
        val values = mutableListOf<Weather?>()
        localStorage.save(Weather(dummyCurrentWeather(), emptyList()))

        //When
        dataRepository.getLastSearchCityWeather().toList(values)

        //Then
        assertThat(values).isNotEmpty()
        assertThat(values.first()?.currentWeather?.location).isEqualTo(dummyCurrentWeather().location)
    }
}