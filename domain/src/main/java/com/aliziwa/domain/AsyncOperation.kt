package com.aliziwa.domain

sealed class AsyncOperation<out Type> {
    /** True if this is a loading state. */
    val isLoading by lazy { this is Loading }

    /** True if this is a completed success state. */
    val isSuccess by lazy { this is Completed && this.result.isSuccess }

    /** True if this is a completed failure state. */
    val isFailure by lazy { this is Completed && this.result.isFailure }

    inline fun onSuccess(action: (data: Type) -> Unit): AsyncOperation<Type> =
        apply {
            if (this is Completed) {
                result.onSuccess(action)
            }
        }

    inline fun onFailure(action: (failure: Throwable) -> Unit): AsyncOperation<Type> =
        apply {
            if (this is Completed) {
                result.onFailure(action)
            }
        }

}

/**
 * [Loading] represents the loading/processing status of an operation. This class does not contain any data
 * since the operation is incomplete
 */
object Loading : AsyncOperation<Nothing>()

/**
 * [Completed] represents the completed status of the operation. It contains a [Result] object that reflects the
 * success or failure of the operation
 *
 * @param T - type of a successful operation
 * @property result - contains the success or failure of the operation
 */
class Completed<T>(val result: Result<T>) : AsyncOperation<T>()

fun <T> loading(): AsyncOperation<T> = Loading
fun <T> success(data: T) = Completed(Result.success(data))
fun <T> failure(failure: Throwable) = Completed<T>(Result.failure(failure))