package punkhomov.grabbi.example.apiweb.requests

import io.ktor.client.request.*
import io.ktor.http.*
import org.jsoup.nodes.Element
import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.example.apiweb.SotkWebApiCall
import punkhomov.grabbi.example.apiweb.models.CardNumber
import punkhomov.grabbi.example.util.takeAsHtml
import punkhomov.grabbi.http.TransformedResponse

class GetMyCardsCall : SotkWebApiCall<List<CardNumber>>() {
    override suspend fun buildHttpRequest(context: ApiCallContext, builder: HttpRequestBuilder) {
        super.buildHttpRequest(context, builder)

        builder.apply {
            method = HttpMethod.Get

            url {
                path("index.php", "passengerlk")
            }
        }
    }

    override fun parseResponse(context: ApiCallContext, response: TransformedResponse): List<CardNumber> {
        val document = response.takeAsHtml()

        return document
            .select("#cardslist .getcardinfo")
            .map(Element::text)
            .map(CardNumber::from)
    }
}