package punkhomov.grabbi.batch

import punkhomov.grabbi.core.ApiCall
import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.core.ApiCallResult

abstract class BatchApiCall<TResult, TBatchCallsMap> : ApiCall<TResult>() {
    protected abstract fun buildCallsMap(context: ApiCallContext, builder: TBatchCallsMap)

    protected abstract fun combineResponses(context: ApiCallContext, responses: BatchCallResponses.ReadAccess): TResult

    protected abstract fun createCallsMap(responses: BatchCallResponses): TBatchCallsMap

    protected abstract suspend fun executeCalls(
        calls: TBatchCallsMap,
        context: ApiCallContext,
        responses: BatchCallResponses.WriteAccess,
    )

    override val service = BatchFeatureService

    override suspend fun execute(context: ApiCallContext): ApiCallResult<TResult> {
        val responses = BatchCallResponses()
        val calls = createCallsMap(responses)
        buildCallsMap(context, calls)

        return try {
            executeCalls(calls, context, responses.writer)

            ApiCallResult.Success(combineResponses(context, responses.reader))
        } catch (ex: BatchInternalCallFailureWrapper) {
            ApiCallResult.Failure(ex.error)
        }
    }

    protected suspend fun executeInternalCall(
        context: ApiCallContext,
        call: QualifiedCall<Any?>,
        responses: BatchCallResponses.WriteAccess,
    ) {
        when (val response = context.client.execute(call.call)) {
            is ApiCallResult.Success -> responses.putResponse(call.qualifier, response.data)
            is ApiCallResult.Failure -> throw BatchInternalCallFailureWrapper(response.error)
        }
    }
}

