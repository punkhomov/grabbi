package punkhomov.grabbi.example.apiweb.models

import java.time.LocalDate

data class CardInfo(
    val number: CardNumber,
    val id: CardId,
    val category: CardCategory,
    val money: Money?, // EP
    val validity: LocalDate?, // SU, LT, else
    val ridesLimit: Int?, // LT, else
    val kind: CardKind,
)