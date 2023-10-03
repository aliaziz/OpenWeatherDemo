package com.aliziwa.domain.usecase.base

import com.aliziwa.domain.AsyncOperation
import com.aliziwa.domain.Completed
import com.aliziwa.domain.Loading
import com.aliziwa.domain.loading
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * [FlowUseCase] is a UseCase type that returns a cold [Flow] with [AsyncOperation] so the status of the ongoing UseCase operation can
 * continuously be observed rather than waiting for the final [Completed].
 *
 * I am choosing to use this approach because this relieves the ViewModel layer from having to notify
 * View layer of a loading state when kicking off the [FlowUseCase] since the ViewModel should be passing the [Flow] from
 * [FlowUseCase] to the View layer directly.
 *
 * In my opinion, this approach excels for simple data fetching scenarios (single repository query -> data transformation -> display data)
 *
 * @param Data - Input type when [FlowUseCase] is being invoked
 * @param T - Return type when [FlowUseCase] successfully completes
 */
abstract class FlowUseCase<in Data, T>(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    /**
     * Public method for executing the business logic of this [FlowUseCase]. Returns a [Flow] that will emit [Loading]
     * and then the result of executing the actual business logic in [FlowUseCase.executeInternal]
     *
     * @return a flow that will emit [Loading] and [Completed]
     */
    fun execute(data: Data): Flow<AsyncOperation<T>> = flow {
        emit(loading())
        emit(withContext(ioDispatcher) { executeInternal(data) })
    }

    /**
     * Protected method for encapsulating the actual business logic of the [FlowUseCase] subtype
     *
     * @return the result of the logic (success or failure)
     */
    protected abstract suspend fun executeInternal(data: Data): Completed<T>
}