package punkhomov.grabbi.http

import io.ktor.client.request.*
import punkhomov.grabbi.core.ApiCall
import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.core.ApiCallResult

/**
 * Represents the extension of [ApiCall] that enables the use of HTTP requests.
 *
 * @param TResult The expected type of result upon successful completion of the call.
 */
abstract class HttpApiCall<TResult> : ApiCall<TResult>() {
    /**
     * Describes the HTTP request using the [io.ktor.client.request.HttpRequestBuilder].
     *
     * @param context The context of the call.
     * @param request The request to configure.
     */
    abstract suspend fun buildHttpRequest(context: ApiCallContext, request: HttpRequestBuilder)

    /**
     * Transforms the format of the [io.ktor.client.statement.HttpResponse] into more
     * suitable formats for parsing.
     *
     * @param context The context of the call.
     * @param response The container to which transformed responses will be added.
     */
    abstract suspend fun transformResponse(context: ApiCallContext, response: TransformedResponse)

    /**
     * Inits a chain of error checkers, allowing for the inclusion of common cases (for
     * the entire hierarchy of calls) and specific cases (for individual calls), or the
     * exclusion of common cases in specific calls.
     *
     * @param context The context of the call.
     * @param chain The chain to init.
     */
    abstract fun initErrorChecks(context: ApiCallContext, chain: ErrorCheckerChain)

    /**
     * Parses the transformed response.
     *
     * @param context The context of the call.
     * @param response The response to parse.
     * @return The result.
     */
    abstract suspend fun parseResponse(context: ApiCallContext, response: TransformedResponse): TResult

    init {
        require(HttpApiService.HTTP_CLIENT)
    }

    override suspend fun execute(context: ApiCallContext): ApiCallResult<TResult> {
        val httpClient = context.getPropOrThrow(HttpApiService.HTTP_CLIENT)
        val httpRequest = createHttpRequest(context)
        val httpResponse = httpClient.request(httpRequest)
        val transformed = TransformedResponse(httpResponse)
        transformResponse(context, transformed)

        checkForErrors(context, transformed)
        val response = parseResponse(context, transformed)
        return ApiCallResult.Success(response)
    }

    /**
     * Creates a [HttpRequestBuilder] and configures it using [buildHttpRequest] for
     * execution with [io.ktor.client.HttpClient].
     *
     * @param context The context of the call.
     * @return HTTP request.
     */
    private suspend fun createHttpRequest(context: ApiCallContext): HttpRequestBuilder {
        return HttpRequestBuilder().also { buildHttpRequest(context, it) }
    }

    /**
     * Creates an [ErrorCheckerChain] and configures it using [initErrorChecks], and
     * utilizes it to check the response for errors.
     *
     * @param context The context of the call.
     * @param response The response to check.
     */
    private fun checkForErrors(context: ApiCallContext, response: TransformedResponse) {
        ErrorCheckerChain().also { initErrorChecks(context, it) }.process(context, response)
    }
}