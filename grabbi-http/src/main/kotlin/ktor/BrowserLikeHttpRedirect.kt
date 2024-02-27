package punkhomov.grabbi.http.ktor

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.HttpRedirect.Plugin.HttpResponseRedirect
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.util.logging.*

private val LOGGER = KtorSimpleLogger("punkhomov.grabbi.http.ktor.BrowserLikeRedirect")

class BrowserLikeHttpRedirect {
    companion object Plugin : HttpClientPlugin<Unit, BrowserLikeHttpRedirect> {
        override val key = AttributeKey<BrowserLikeHttpRedirect>("BrowserLikeRedirect")

        override fun prepare(block: Unit.() -> Unit): BrowserLikeHttpRedirect {
            return BrowserLikeHttpRedirect()
        }

        override fun install(plugin: BrowserLikeHttpRedirect, scope: HttpClient) {
            scope.plugin(HttpSend).intercept { context ->
                val origin = execute(context)
                handleCall(context, origin, scope)
            }
        }

        @OptIn(InternalAPI::class)
        private suspend fun Sender.handleCall(
            context: HttpRequestBuilder,
            origin: HttpClientCall,
            client: HttpClient
        ): HttpClientCall {
            if (!origin.response.status.isRedirect()) return origin

            var call = origin
            var requestBuilder = context
            val originAuthority = origin.request.url.authority

            while (true) {
                client.monitor.raise(HttpResponseRedirect, call.response)

                val location = call.response.headers[HttpHeaders.Location]
                LOGGER.trace("Received redirect response to $location for request ${context.url}")

                requestBuilder = HttpRequestBuilder().apply {
                    takeFromWithExecutionContext(requestBuilder)

                    method = HttpMethod.Get
                    url.parameters.clear()
                    body = EmptyContent
                    bodyType = null

                    location?.let { url.takeFrom(it) }

                    if (originAuthority != url.authority) {
                        headers.remove(HttpHeaders.Authorization)
                        LOGGER.trace("Removing Authorization header from redirect for ${context.url}")
                    }
                }

                call = execute(requestBuilder)
                if (!call.response.status.isRedirect()) return call
            }
        }
    }
}

private fun HttpStatusCode.isRedirect(): Boolean = when (value) {
    HttpStatusCode.MovedPermanently.value,
    HttpStatusCode.Found.value,
    HttpStatusCode.TemporaryRedirect.value,
    HttpStatusCode.PermanentRedirect.value,
    HttpStatusCode.SeeOther.value -> true

    else -> false
}