package punkhomov.grabbi.example.apiweb.requests

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.client.request.*
import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.core.respondError
import punkhomov.grabbi.example.apiweb.SotkWebApiCall
import punkhomov.grabbi.example.apiweb.SotkWebApiException
import punkhomov.grabbi.example.apiweb.SotkWebService
import punkhomov.grabbi.example.apiweb.models.*
import punkhomov.grabbi.example.util.ifPresentOnlyAsString
import punkhomov.grabbi.example.util.takeAsJson
import punkhomov.grabbi.http.ApiErrorChecker
import punkhomov.grabbi.http.ErrorCheckerChain
import punkhomov.grabbi.http.TransformedResponse
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GetCardInfoCall(val cardNumber: CardNumber) : SotkWebApiCall<CardInfo>() {
    init {
        require(SotkWebService.TOKEN)
    }

    override suspend fun buildHttpRequest(context: ApiCallContext, request: HttpRequestBuilder) {
        super.buildHttpRequest(context, request)

        val token = context.getPropOrThrow(SotkWebService.TOKEN)

        request.apply {
            configureAjaxRequest()

            setBodyAjaxForm(AjaxOperation.GET_CARD_INFO, cardNumber, token)
        }
    }

    override fun initErrorChecks(context: ApiCallContext, chain: ErrorCheckerChain) {
        super.initErrorChecks(context, chain)

    }

    override suspend fun parseResponse(context: ApiCallContext, response: TransformedResponse): CardInfo {
        val json = response.takeAsJson().asJsonObject

        val dataString = json.get("data").asString
        val dataJson = JsonParser.parseString(dataString).asJsonObject

        return parseCardInfo(dataJson, cardNumber)
    }

    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        private fun parseCardInfo(json: JsonObject, cardNumber: CardNumber): CardInfo {
            return CardInfo(
                number = cardNumber,
                id = parseCardId(json),
                category = parseCardCategory(json),
                money = parseBalance(json),
                validity = parseValidity(json),
                ridesLimit = parseRidesLimit(json),
                kind = parseCardKind(json),
            )
        }

        private fun parseCardId(dataJson: JsonObject): CardId {
            return dataJson.get("short").asString.let(CardId::from)
        }

        private fun parseCardCategory(json: JsonObject): CardCategory {
            return CardCategory(
                id = json.get("ctg").asInt,
                description = json.get("ctgdesc").asString
            )
        }

        private fun parseBalance(json: JsonObject): Money? = runCatching {
            json.get("balance").asString.let(Money::parse)
        }.getOrNull()

        private fun parseValidity(json: JsonObject): LocalDate? = runCatching {
            json.get("balance").asString.let { LocalDateTime.parse(it, formatter) }.toLocalDate()
        }.getOrNull()

        private fun parseRidesLimit(json: JsonObject): Int? {
            return json.get("st_limit").takeUnless { it.isJsonNull }?.asString?.toInt()
        }

        private fun parseCardKind(json: JsonObject): CardKind {
            return json.get("type").takeUnless { it.isJsonNull }?.asString.let(CardKind::of)
        }
    }
}

class GetCardInfoError : SotkWebApiException()

object CheckForGetCardInfoError : ApiErrorChecker {
    private const val ERROR_MESSAGE =
        "Ошибка запроса:"

    override fun check(context: ApiCallContext, response: TransformedResponse) {
        response.ifPresentOnlyAsString {
            if (it.contains(ERROR_MESSAGE)) {
                context.respondError(GetCardInfoError())
            }
        }
    }
}


