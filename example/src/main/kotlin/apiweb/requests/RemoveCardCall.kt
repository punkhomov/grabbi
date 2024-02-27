package punkhomov.grabbi.example.apiweb.requests

import io.ktor.client.request.*
import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.example.apiweb.SotkWebApiCall
import punkhomov.grabbi.example.apiweb.SotkWebService
import punkhomov.grabbi.example.apiweb.models.CardNumber
import punkhomov.grabbi.http.TransformedResponse

class RemoveCardCall(val cardNumber: CardNumber) : SotkWebApiCall<Unit>() {
    init {
        require(SotkWebService.TOKEN)
    }

    override suspend fun buildHttpRequest(context: ApiCallContext, builder: HttpRequestBuilder) {
        super.buildHttpRequest(context, builder)

        val token = context.getPropOrThrow(SotkWebService.TOKEN)

        builder.apply {
            configureAjaxRequest()

            setBodyAjaxForm(AjaxOperation.REMOVE_CARD, cardNumber, token)
        }
    }

    override fun parseResponse(context: ApiCallContext, response: TransformedResponse) {
        return
    }
}

