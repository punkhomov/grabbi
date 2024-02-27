package punkhomov.grabbi.batch

import kotlinx.coroutines.coroutineScope
import punkhomov.grabbi.core.ApiCallContext

abstract class BatchSequentialApiCall<Result> : BatchApiCall<Result, BatchCallsMapWithResponses>() {
    override fun createCallsMap(responses: BatchCallResponses): BatchCallsMapWithResponses {
        return BatchCallsMapWithResponses(responses.reader)
    }

    override suspend fun executeCalls(
        calls: BatchCallsMapWithResponses,
        context: ApiCallContext,
        responses: BatchCallResponses.WriteAccess
    ): Unit = coroutineScope {
        calls.forEach {
            executeInternalCall(context, it, responses)
        }
    }
}