package punkhomov.grabbi.example.apiweb.props

import org.jsoup.nodes.Document

data class Token(
    val csrf: String,
    val pid: String,
) {
    companion object {
        fun from(document: Document) = Token(
            csrf = document.expectFirst("input#token").attr("name"),
            pid = document.expectFirst("input#pid").attr("name"),
        )
    }
}