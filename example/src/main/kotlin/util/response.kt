package punkhomov.grabbi.example.util

import com.google.gson.JsonElement
import org.jsoup.nodes.Document
import punkhomov.grabbi.http.TransformedResponse
import punkhomov.grabbi.http.getOrThrowAs
import punkhomov.grabbi.http.ifPresentAs
import punkhomov.grabbi.http.ifPresentOnlyAs

inline fun TransformedResponse.takeAsString(): String = getOrThrowAs()

inline fun TransformedResponse.takeAsHtml(): Document = getOrThrowAs()

inline fun TransformedResponse.takeAsJson(): JsonElement = getOrThrowAs()


inline fun TransformedResponse.ifPresentAsString(block: TransformedResponse.(String) -> Unit) =
    ifPresentAs(String::class, block)

inline fun TransformedResponse.ifPresentAsHtml(block: TransformedResponse.(Document) -> Unit) =
    ifPresentAs(Document::class, block)

inline fun TransformedResponse.ifPresentAsJson(block: TransformedResponse.(String) -> Unit) =
    ifPresentAs(String::class, block)


inline fun TransformedResponse.ifPresentOnlyAsString(block: TransformedResponse.(String) -> Unit) =
    ifPresentOnlyAs(String::class, block)