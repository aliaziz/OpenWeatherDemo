package com.aliziwa.data.repository

import com.aliziwa.data.repository.local.storage.WeatherStorage
import com.aliziwa.domain.model.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow


/**
 * Test implementation of local storage to assert tests with
 */
internal val testDataState = MutableStateFlow<Weather?>(null)
internal val localStorage: WeatherStorage<Weather> by lazy {
    object : WeatherStorage<Weather> {
        override suspend fun save(data: Weather): Flow<Boolean> {
            testDataState.value = data
            return flow { true }
        }

        override suspend fun get(): Flow<Weather> {
            return flow { testDataState.value?.let { emit(it) } }
        }
    }
}