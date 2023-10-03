package com.aliziwa.domain.usecase.base

import kotlinx.coroutines.flow.Flow

/**
 * [UseCase] - An abstraction on top of all usecases that return flows
 *
 * @param T - Represents the input to the request
 * @param R - Represents the out wrapped in a flow the caller expects back
 *
 */
abstract class UseCase<T, R> {
    /**
     * Call that invokes the actual flow
     *
     * @return [Flow<R>]
     */
    abstract suspend operator fun invoke(input: T): Flow<R>
}