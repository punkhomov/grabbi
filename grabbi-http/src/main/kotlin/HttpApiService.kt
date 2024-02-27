package punkhomov.grabbi.http

import io.ktor.client.*
import punkhomov.grabbi.core.ApiCall
import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.core.ApiService
import punkhomov.grabbi.core.createPropKey

abstract class HttpApiService : ApiService() {
    protected abstract val httpClient: HttpClient

    override suspend fun buildContext(call: ApiCall<*>, context: ApiCallContext) {
        context.setProp(HTTP_CLIENT, httpClient)
    }

    companion object {
        val HTTP_CLIENT = createPropKey<HttpClient>("HTTP_CLIENT")
    }
}