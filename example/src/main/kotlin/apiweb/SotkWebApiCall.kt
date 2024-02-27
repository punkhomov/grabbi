package punkhomov.grabbi.example.apiweb

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.core.respondError
import punkhomov.grabbi.example.apiweb.errors.InvalidAccessTokenError
import punkhomov.grabbi.example.apiweb.errors.InvalidActionTokenError
import punkhomov.grabbi.example.util.ifPresentAsHtml
import punkhomov.grabbi.example.util.ifPresentOnlyAsString
import punkhomov.grabbi.example.util.takeAsString
import punkhomov.grabbi.http.*

abstract class SotkWebApiCall<Result> : HttpApiCall<Result>() {
    override val service = SotkWebService

    override suspend fun buildHttpRequest(context: ApiCallContext, builder: HttpRequestBuilder) {
        builder.apply {
            url {
                protocol = URLProtocol.HTTPS
                host = "www.s-otk.ru"
            }
        }
    }

    override fun initErrorChecks(context: ApiCallContext, chain: ErrorCheckerChain) {
        chain.append(CheckForInvalidActionToken)
        chain.append(CheckForInvalidAuthToken)
    }

    override suspend fun transformResponse(context: ApiCallContext, response: TransformedResponse) {
        response.tryTransform { httpResponse.bodyAsText() }
        response.tryTransform { parseHtml(takeAsString()) }
        response.tryTransform { parseJson(takeAsString()) }
    }

    private fun parseHtml(body: String): Document {
        return Jsoup.parse(body)
    }

    private fun parseJson(body: String): JsonElement {
        return JsonParser.parseString(body)
    }
}


object CheckForInvalidActionToken : ApiErrorChecker {
    private const val INVALID_CSRF_TOKEN_MESSAGE1 =
        "Уппс! Вам тут не рады"
    private const val INVALID_CSRF_TOKEN_MESSAGE2 = "Маркер безопасности не прошел проверку. " +
            "Запрос был прерван, чтобы предотвратить любое нарушение безопасности. Пожалуйста, попробуйте снова."

    override fun check(context: ApiCallContext, response: TransformedResponse) {
        response.ifPresentOnlyAsString {
            if (it.contains(INVALID_CSRF_TOKEN_MESSAGE1)) {
                val invalidCsrfToken = context.getPropOrNull(SotkWebService.TOKEN)
                context.respondError(InvalidActionTokenError(token = invalidCsrfToken))
            }
        }

        response.ifPresentAsHtml {
            val message = it.selectFirst("#system-message .alert-message")?.text()
            if (message == INVALID_CSRF_TOKEN_MESSAGE2) {
                val invalidCsrfToken = context.getPropOrNull(SotkWebService.TOKEN)
                context.respondError(InvalidActionTokenError(token = invalidCsrfToken))
            }
        }
    }
}

object CheckForInvalidAuthToken : ApiErrorChecker {
    private const val LOGIN_PAGE_MESSAGE =
        "Для просмотра информации необходимо зайти в аккаунт"

    override fun check(context: ApiCallContext, response: TransformedResponse) {
        response.ifPresentAsHtml {
            val message = it.selectFirst("#component h5")?.text()
            if (message == LOGIN_PAGE_MESSAGE) {
                context.respondError(InvalidAccessTokenError())
            }
        }
    }
}