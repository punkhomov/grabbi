package punkhomov.grabbi.http.ktor

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

fun HttpRequestBuilder.setBodyForm(form: ParametersBuilder.() -> Unit) {
    setBody(FormDataContent(Parameters.build(form)))
}