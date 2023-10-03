package com.aliziwa.data.repository.local.storage

import kotlinx.coroutines.flow.Flow

/**
 * A contract to define what we can store locally.
 *
 * [T] defines the type of object we would like to save
 */
interface WeatherStorage<T> {
    /**
     * Represents a call to save the specified object [T].
     *
     * @param data - The data to be saved. I'd scope this to a specific type if I had more time
     * @return [Flow] - True or false if object is saved. Currently we shall return true only. Update later.
     */
    suspend fun save(data: T): Flow<Boolean>

    /**
     * Represents a call to retrieve the saved type [T]
     *
     * @return - Flow of T. No check for if the object is null because for this challenge, we are only using [ProtoDataStore] which forces us to
     * return a defaultInstance of [T]
     */
    suspend fun get(): Flow<T>
}