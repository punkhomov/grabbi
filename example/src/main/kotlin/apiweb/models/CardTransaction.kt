package punkhomov.grabbi.example.apiweb.models

import java.time.LocalDateTime

data class CardTransaction(
    val time: LocalDateTime,
    val cost: Money?, // поездки для LT
    val route: String,
    val vehicle: Vehicle,
)