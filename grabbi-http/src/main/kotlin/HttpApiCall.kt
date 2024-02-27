package punkhomov.grabbi.http

import io.ktor.client.request.*
import punkhomov.grabbi.core.ApiCall
import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.core.ApiCallResult

abstract class HttpApiCall<Result> : ApiCall<Result>() {
    abstract suspend fun buildHttpRequest(context: ApiCallContext, builder: HttpRequestBuilder)

    abstract suspend fun transformResponse(context: ApiCallContext, response: TransformedResponse)

    abstract fun initErrorChecks(context: ApiCallContext, chain: ErrorCheckerChain)

    abstract suspend fun parseResponse(context: ApiCallContext, response: TransformedResponse): Result

    init {
        require(HttpApiService.HTTP_CLIENT)
    }

    override suspend fun execute(context: ApiCallContext): ApiCallResult<Result> {
        val httpClient = context.getPropOrThrow(HttpApiService.HTTP_CLIENT)
        val httpRequest = createHttpRequest(context)
        val httpResponse = httpClient.request(httpRequest)
        val transformed = TransformedResponse(httpResponse)
        transformResponse(context, transformed)

        checkForErrors(context, transformed)
        val response = parseResponse(context, transformed)
        return ApiCallResult.Success(response)
    }

    private suspend fun createHttpRequest(context: ApiCallContext): HttpRequestBuilder {
        return HttpRequestBuilder().also { buildHttpRequest(context, it) }
    }

    private fun checkForErrors(context: ApiCallContext, response: TransformedResponse) {
        ErrorCheckerChain().also { initErrorChecks(context, it) }.process(context, response)
    }
}