package punkhomov.grabbi.example.util

import org.jsoup.nodes.Element

fun Element.expectByXpath(xpath: String) =
    selectXpath(xpath).first() ?: error("No elements matched the xpath '$xpath'")

fun Element.expectByCssQuery(cssQuery: String) =
    select(cssQuery).first() ?: error("No elements matched the css query '$cssQuery'")

fun Element.selectByXpath(xpath: String) =
    selectXpath(xpath)

fun Element.selectByCssQuery(cssQuery: String) =
    select(cssQuery)