package com.aliziwa.data.repository.local

import com.aliziwa.data.LocalCurrentWeather
import com.aliziwa.data.LocalWeatherDetails
import com.aliziwa.domain.model.DummyData.dummyCurrentWeather
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class OpenWeatherExtKtTest {
    @Test
    fun `test toLocalCurrentWeather`() {
        //Given
        val currentWeather = dummyCurrentWeather()

        //When
        val actual = currentWeather.toLocalCurrentWeather()

        //Then
        with(actual) {
            assertThat(location).isEqualTo(currentWeather.location)
            assertThat(description).isEqualTo(currentWeather.description)
            assertThat(details.currentTemp).isEqualTo(currentWeather.details.currentTemp)
        }
    }

    @Test
    fun `test toCurrentWeather`() {
        //Given
        val localCurrentWeather = dummyLocalWeather()

        //When
        val actual = localCurrentWeather.toCurrentWeather()

        //Then
        with(actual) {
            assertThat(location).isEqualTo(localCurrentWeather.location)
            assertThat(description).isEqualTo(localCurrentWeather.description)
            assertThat(details.currentTemp).isEqualTo(localCurrentWeather.details.currentTemp)
        }
    }
}

fun dummyLocalWeather(): LocalCurrentWeather = LocalCurrentWeather.newBuilder()
    .setLocation("New York")
    .setDetails(LocalWeatherDetails.newBuilder()
        .setIcon("09d")
        .setHighTemp("12")
        .setLowTemp("9")
        .setCurrentTemp("11")
    ).build()