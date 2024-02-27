package punkhomov.grabbi.batch

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import punkhomov.grabbi.core.ApiCallContext

abstract class BatchParallelApiCall<Result> : BatchApiCall<Result, BatchCallsMapWithUnit>() {
    override fun createCallsMap(responses: BatchCallResponses): BatchCallsMapWithUnit {
        return BatchCallsMapWithUnit(Unit)
    }

    override suspend fun executeCalls(
        calls: BatchCallsMapWithUnit,
        context: ApiCallContext,
        responses: BatchCallResponses.WriteAccess
    ): Unit = coroutineScope {
        calls.map {
            async {
                executeInternalCall(context, it, responses)
            }
        }.awaitAll()
    }
}