package punkhomov.grabbi.example.apiweb.requests

import io.ktor.client.request.*
import io.ktor.http.*
import punkhomov.grabbi.example.apiweb.models.CardNumber
import punkhomov.grabbi.example.apiweb.props.Token
import punkhomov.grabbi.http.ktor.setBodyForm

fun HttpRequestBuilder.configureAjaxRequest() {
    method = HttpMethod.Post

    url {
        path("index.php", "index.php")

        parameter("option", "com_ajax")
        parameter("module", "lkabinet")
        parameter("format", "json")
    }
}

fun HttpRequestBuilder.setBodyAjaxForm(
    operation: Int,
    cardNumber: CardNumber,
    token: Token,
    ext: ParametersBuilder.() -> Unit = {}
) = setBodyForm {
    append("operation", operation.toString())
    append("card", cardNumber.value)
    append(token.csrf, "1")
    append("pid", token.pid)

    ext()
}

object AjaxOperation {
    const val ADD_CARD = 1
    const val REMOVE_CARD = 2
    const val GET_CARD_TRANSACTIONS = 3
    const val GET_PAYMENT_INFO = 4
    const val SEND_PAYMENT_DATA = 5
    const val GET_CARD_INFO = 6
}