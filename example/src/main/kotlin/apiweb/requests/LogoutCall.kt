package punkhomov.grabbi.example.apiweb.requests

import io.ktor.client.request.*
import io.ktor.http.*
import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.example.apiweb.SotkWebApiCall
import punkhomov.grabbi.example.apiweb.SotkWebService
import punkhomov.grabbi.http.TransformedResponse
import punkhomov.grabbi.http.ktor.setBodyForm

class LogoutCall : SotkWebApiCall<Unit>() {
    init {
        require(SotkWebService.TOKEN)
    }

    override suspend fun buildHttpRequest(context: ApiCallContext, builder: HttpRequestBuilder) {
        super.buildHttpRequest(context, builder)

        val token = context.getPropOrThrow(SotkWebService.TOKEN)

        builder.apply {
            method = HttpMethod.Post

            url {
                path("index.php", "passengerlk")
            }

            setBodyForm {
                append("option", "com_users")
                append("task", "user.logout")
                append("return", "aW5kZXgucGhwP0l0ZW1pZD0xMDE=")
                append(token.csrf, "1")
            }
        }
    }

    override fun parseResponse(context: ApiCallContext, response: TransformedResponse) {
        return
    }
}