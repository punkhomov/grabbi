package punkhomov.grabbi.example.apiweb

import io.ktor.client.*
import io.ktor.client.engine.apache5.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cookies.*
import punkhomov.grabbi.core.*
import punkhomov.grabbi.example.DEFAULT_USER_AGENT
import punkhomov.grabbi.example.apiweb.errors.InvalidAccessTokenError
import punkhomov.grabbi.example.apiweb.errors.InvalidActionTokenError
import punkhomov.grabbi.example.apiweb.props.Token
import punkhomov.grabbi.example.apiweb.props.TokenProp
import punkhomov.grabbi.http.HttpApiService
import punkhomov.grabbi.http.ktor.BrowserLikeHttpRedirect

class SotkWebService(config: SotkWebConfig) : HttpApiService() {
    private var token = TokenProp()

    override val errorHandlingStrategy get() = super.errorHandlingStrategy

    override val httpClient: HttpClient = HttpClient(Apache5) {
        engine {
            followRedirects = false
        }

        install(UserAgent) {
            agent = DEFAULT_USER_AGENT
        }
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
        install(BrowserLikeHttpRedirect)
    }

    override suspend fun buildContext(call: ApiCall<*>, context: ApiCallContext) {
        super.buildContext(call, context)

        if (call.requirements.contains(TOKEN)) {
            val value = token.getValueOrElse(context) { context.respondError(it) }
            context.setProp(TOKEN, value)
        }
    }

    override suspend fun onErrorOccurred(context: ApiCallContext, error: Exception): Boolean {
        return when (error) {
            is InvalidAccessTokenError -> false // refreshToken
            is InvalidActionTokenError -> token.invalidateValue(context, error.token)
            else -> false
        }
    }

    suspend fun resetToken() {
        token.clearValue()
    }

    class SotkWebConfig {
        var authManager: SotkWebAuthManager? = null
    }

    companion object Factory : ApiService.Factory<SotkWebConfig, SotkWebService> {
        override val key = createServiceKey()

        override fun createService(configure: SotkWebConfig.() -> Unit): SotkWebService {
            return SotkWebService(SotkWebConfig().apply(configure))
        }

        val TOKEN = createPropKey<Token>("TOKEN")
    }
}