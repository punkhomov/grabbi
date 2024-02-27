package punkhomov.grabbi.example.apiweb.models

@JvmInline
value class CardId private constructor(val value: String) {
    val cn9Number: CardNumber
        get() {
            return CardNumber.from(value.drop(1))
        }

    companion object {
        fun from(id: String): CardId {
            require(id.length == 10) { "Malformed card id." }
            return CardId(id)
        }
    }
}