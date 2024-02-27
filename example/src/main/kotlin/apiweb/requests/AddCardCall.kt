package punkhomov.grabbi.example.apiweb.requests

import io.ktor.client.request.*
import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.example.apiweb.SotkWebApiCall
import punkhomov.grabbi.example.apiweb.SotkWebService
import punkhomov.grabbi.example.apiweb.models.CardNumber
import punkhomov.grabbi.http.TransformedResponse

class AddCardCall(val cardNumber: CardNumber) : SotkWebApiCall<Unit>() {
    init {
        require(SotkWebService.TOKEN)
    }

    override suspend fun buildHttpRequest(context: ApiCallContext, request: HttpRequestBuilder) {
        super.buildHttpRequest(context, request)

        val token = context.getPropOrThrow(SotkWebService.TOKEN)

        request.apply {
            configureAjaxRequest()

            setBodyAjaxForm(AjaxOperation.ADD_CARD, cardNumber, token)
        }
    }

    override suspend fun parseResponse(context: ApiCallContext, response: TransformedResponse) {
        return
    }
}