package com.aliziwa.domain


/**
 * [NetworkConnectionFailure] used to represent network timeouts or other failure to use the network
 *
 * @param errorData any error data in the response
 */
open class NetworkConnectionFailure(val errorData: String?) : Throwable()

/**
 * [Failure] represents any failure with in the app.
 *
 * @param errorData any error data in the response
 */
open class Failure(val errorData: String? = null) : Throwable()

enum class RemoteDataFailureMessage {
    REQUEST_FAILED,
    INTERNET_FAILURE,
    EMPTY_RESPONSE
}

enum class LocalDataFailureMessage {
    NO_CACHED_DATA,
    FAILED_TO_SAVE,
    INVALID_REQUEST
}