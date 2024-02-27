package punkhomov.grabbi.example.apiweb.requests

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import punkhomov.grabbi.batch.BatchCallResponses
import punkhomov.grabbi.batch.BatchCallsMapWithResponses
import punkhomov.grabbi.batch.BatchSequentialApiCall
import punkhomov.grabbi.batch.qualifierKey
import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.core.respondError
import punkhomov.grabbi.example.apiweb.CheckForInvalidAuthToken
import punkhomov.grabbi.example.apiweb.SotkWebApiCall
import punkhomov.grabbi.example.apiweb.SotkWebApiException
import punkhomov.grabbi.example.util.expectByXpath
import punkhomov.grabbi.example.util.ifPresentAsHtml
import punkhomov.grabbi.example.util.takeAsHtml
import punkhomov.grabbi.http.ApiErrorChecker
import punkhomov.grabbi.http.ErrorCheckerChain
import punkhomov.grabbi.http.TransformedResponse

class LoginCall(
    val username: String, val password: String, val rememberMe: Boolean,
) : BatchSequentialApiCall<LoggedInUser>() {
    companion object {
        private val GET_TOKEN = qualifierKey(String::class, "GET_TOKEN")
        private val LOGIN = qualifierKey(LoggedInUser::class, "LOGIN")
    }

    override fun buildCallsMap(context: ApiCallContext, builder: BatchCallsMapWithResponses) {
        builder.apply {
            call(GET_TOKEN) { GetLoginFormTokenCall() }
            call(LOGIN) {
                val token = getResponse(GET_TOKEN)
                LoginFormSumbitCall(username, password, rememberMe, token)
            }
        }
    }

    override fun combineResponses(context: ApiCallContext, responses: BatchCallResponses.ReadAccess): LoggedInUser {
        return responses.getResponse(LOGIN)
    }
}


private class GetLoginFormTokenCall : SotkWebApiCall<String>() {
    override suspend fun buildHttpRequest(context: ApiCallContext, request: HttpRequestBuilder) {
        super.buildHttpRequest(context, request)

        request.apply {
            method = HttpMethod.Get

            url {
                path("index.php", "passengerlk")
            }
        }
    }

    override fun initErrorChecks(context: ApiCallContext, chain: ErrorCheckerChain) {
        super.initErrorChecks(context, chain)
        // todo cancel check for auth
    }

    override suspend fun parseResponse(context: ApiCallContext, response: TransformedResponse): String {
        val document = response.takeAsHtml()

        return document.expectByXpath(TOKEN_XPATH).attr("name")
    }

    companion object {
        private val TOKEN_XPATH = "//*[@id='lp-popup']/form/input[string-length(@name)=32]"
    }
}


private class LoginFormSumbitCall(
    val username: String, val password: String, val rememberMe: Boolean, val token: String
) : SotkWebApiCall<LoggedInUser>() {
    override suspend fun buildHttpRequest(context: ApiCallContext, request: HttpRequestBuilder) {
        super.buildHttpRequest(context, request)

        request.apply {
            method = HttpMethod.Post

            url {
                path("index.php", "passengerlk")
            }

            setBody(FormDataContent(parameters {
                append("username", username)
                append("password", password)
                append("remember", "on")
                append("option", "com_users")
                append("task", "user.login")
                append("return", "aW5kZXgucGhwP0l0ZW1pZD0xMTk=")
                append(token, "1")
            }))
        }
    }

    override fun initErrorChecks(context: ApiCallContext, chain: ErrorCheckerChain) {
        super.initErrorChecks(context, chain)
        chain.append(CheckForInvalidCredentials)
        chain.cancel(CheckForInvalidAuthToken)
    }

    override suspend fun parseResponse(context: ApiCallContext, response: TransformedResponse): LoggedInUser {
        val html = response.takeAsHtml()
        val username = html.expectFirst(".lk_header h5").text().drop(14)
        return LoggedInUser(username)
    }
}


object CheckForInvalidCredentials : ApiErrorChecker {
    private const val INVALID_CREDENTIALS_MESSAGE =
        "Логин или пароль введены неправильно, либо такой учётной записи ещё не существует."

    override fun check(context: ApiCallContext, response: TransformedResponse) {
        response.ifPresentAsHtml {
            val message = it.selectFirst("#system-message .alert-message")?.text()
            if (message == INVALID_CREDENTIALS_MESSAGE) {
                context.respondError(InvalidCredentialsError())
            }
        }
    }
}

class InvalidCredentialsError : SotkWebApiException()


class LoggedInUser(val username: String)