package com.aliziwa.domain

/**
 * [LocalDataFailure] represents an error on the client-side. Invalid request params or local errors caught on the FE.
 *
 * @param failureMessage - A [LocalDataFailureMessage].
 */
sealed class LocalDataFailure(failureMessage: LocalDataFailureMessage) : Failure(errorData = failureMessage.name) {
    /**
     * [NoCachedDataFailure] represents an error that occurs during the retrieval of local cache data
     */
    object NoCachedDataFailure : LocalDataFailure(LocalDataFailureMessage.NO_CACHED_DATA)

    /**
     * [InvalidDataFailure] represents an error that occurs when a client makes a request with invalid data.
     */
    object InvalidDataFailure : LocalDataFailure(LocalDataFailureMessage.INVALID_REQUEST)
}