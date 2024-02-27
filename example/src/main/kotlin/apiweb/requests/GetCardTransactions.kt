package punkhomov.grabbi.example.apiweb.requests

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.ktor.client.request.*
import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.example.apiweb.SotkWebApiCall
import punkhomov.grabbi.example.apiweb.SotkWebService
import punkhomov.grabbi.example.apiweb.models.CardNumber
import punkhomov.grabbi.example.apiweb.models.CardTransaction
import punkhomov.grabbi.example.apiweb.models.Money
import punkhomov.grabbi.example.apiweb.models.Vehicle
import punkhomov.grabbi.example.util.takeAsJson
import punkhomov.grabbi.http.TransformedResponse
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GetCardTransactions(
    val cardNumber: CardNumber, val startDate: LocalDate, val endDate: LocalDate
) : SotkWebApiCall<List<CardTransaction>>() {
    init {
        require(SotkWebService.TOKEN)
    }

    override suspend fun buildHttpRequest(context: ApiCallContext, request: HttpRequestBuilder) {
        super.buildHttpRequest(context, request)

        val token = context.getPropOrThrow(SotkWebService.TOKEN)

        request.apply {
            configureAjaxRequest()

            setBodyAjaxForm(AjaxOperation.GET_CARD_TRANSACTIONS, cardNumber, token) {
                append("startday", dFormatter.format(startDate))
                append("endday", dFormatter.format(endDate))
            }
        }
    }

    override suspend fun parseResponse(context: ApiCallContext, response: TransformedResponse): List<CardTransaction> {
        val json = response.takeAsJson().asJsonObject

        return json.getAsJsonArray("data").asSequence()
            .map(JsonElement::getAsJsonObject)
            .map(::parseCardTransaction)
            .toList()
    }


    companion object {
        private val dFormatter = DateTimeFormatter.ofPattern("yyyy-M-d")
        private val dtFormatter = DateTimeFormatter.ofPattern("dd MM yyyy HH:mm:ss")

        private fun parseCardTransaction(json: JsonObject): CardTransaction {
            return CardTransaction(
                time = parseTime(json),
                cost = parseCost(json),
                route = parseRoute(json),
                vehicle = parseVehicle(json),
            )
        }

        private fun parseTime(json: JsonObject): LocalDateTime {
            return json.get("dt").asString.let { LocalDateTime.parse(it, dtFormatter) }
        }

        private fun parseCost(json: JsonObject): Money? {
            return json.get("sum").asString.takeIf { it != "1" }?.let(Money::parse)
        }

        private fun parseRoute(json: JsonObject): String {
            return json.get("code").asString
        }

        private fun parseVehicle(json: JsonObject): Vehicle {
            return json.get("vichle").asString.let(Vehicle::from)
        }
    }
}