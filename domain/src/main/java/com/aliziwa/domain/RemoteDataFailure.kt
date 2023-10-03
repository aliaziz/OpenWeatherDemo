package com.aliziwa.domain

/**
 * [RemoteDataFailure] represents any failure on the server-side.  Typical usage is whenever an HTTP 4XX
 *  * or 5XX status code is encountered
 *
 *  @param failureMessage - A failure message type. Am using an enum but it would be better to pass the actual server error
 *  here.
 */
sealed class RemoteDataFailure(val failureMessage: RemoteDataFailureMessage) : Failure(errorData = failureMessage.name) {

    /**
     * [RequestFailure] represents an error that occurs when an API request fails. - It's too generic. Can be scoped down further if
     * needed.
     */
    object RequestFailure: RemoteDataFailure(RemoteDataFailureMessage.REQUEST_FAILED)

    /**
     * [EmptyResponseFailure] represents an error that occurs when a response returns no data, yet it should have data.
     */
    object EmptyResponseFailure: RemoteDataFailure(RemoteDataFailureMessage.EMPTY_RESPONSE)

    /**
     * [InternetFailure] represents an error that occurs when a response returns no data, yet it should have data.
     */
    object InternetFailure: RemoteDataFailure(RemoteDataFailureMessage.INTERNET_FAILURE)
}