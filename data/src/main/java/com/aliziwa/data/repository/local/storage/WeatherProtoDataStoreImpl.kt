package com.aliziwa.data.repository.local.storage

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.aliziwa.data.LocalWeather
import com.aliziwa.data.repository.local.toCurrentWeather
import com.aliziwa.data.repository.local.toForecastWeather
import com.aliziwa.data.repository.local.toLocalCurrentWeather
import com.aliziwa.domain.model.Weather
import com.google.protobuf.InvalidProtocolBufferException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * Representation of [DataStore<Weather>]
 */
@VisibleForTesting
val Context.localDataStore: DataStore<LocalWeather> by dataStore(
    fileName = "weather_cache.pb",
    serializer = WeatherProtoDataStoreImpl.LocalOpenWeatherSerializer
)

/**
 * An implementation of the [WeatherStorage] contract that uses a [ProtoDataStore]
 */
class WeatherProtoDataStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : WeatherStorage<Weather> {

    override suspend fun save(data: Weather): Flow<Boolean> {
        context.localDataStore.updateData { localWeather ->
            val builder = localWeather.toBuilder()
                .setCurrentWeather(data.currentWeather.toLocalCurrentWeather())
                .build()
            builder
        }
        return flow { emit(true) }
    }

    override suspend fun get(): Flow<Weather> {
        return context.localDataStore.data.map { localWeather ->
            Weather(
                currentWeather = localWeather.currentWeather.toCurrentWeather(),
                forecastWeather = localWeather.forecastWeatherList.map { it.toForecastWeather() }
            )
        }
    }

    /**
     * LocalSerializer for the proto DataStore
     */
    object LocalOpenWeatherSerializer : Serializer<LocalWeather> {
        override val defaultValue: LocalWeather = LocalWeather.getDefaultInstance()

        override suspend fun readFrom(input: InputStream): LocalWeather {
            try {
                return LocalWeather.parseFrom(input)
            } catch (e: InvalidProtocolBufferException) {
                throw CorruptionException("Failed to read proto ", e)
            }
        }

        override suspend fun writeTo(t: LocalWeather, output: OutputStream) = t.writeTo(output)

    }
}