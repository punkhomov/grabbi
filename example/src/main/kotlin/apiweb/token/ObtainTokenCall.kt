package punkhomov.grabbi.example.apiweb.props

import io.ktor.client.request.*
import io.ktor.http.*
import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.example.apiweb.SotkWebApiCall
import punkhomov.grabbi.example.util.takeAsHtml
import punkhomov.grabbi.http.TransformedResponse

class ObtainTokenCall : SotkWebApiCall<Token>() {
    override suspend fun buildHttpRequest(context: ApiCallContext, request: HttpRequestBuilder) {
        super.buildHttpRequest(context, request)

        request.apply {
            method = HttpMethod.Get

            url {
                path("index.php", "passengerlk")
            }
        }
    }

    override suspend fun parseResponse(context: ApiCallContext, response: TransformedResponse): Token {
        val document = response.takeAsHtml()

        return Token.from(document)
    }
}