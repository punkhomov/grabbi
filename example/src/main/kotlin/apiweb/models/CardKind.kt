package punkhomov.grabbi.example.apiweb.models

@JvmInline
value class CardKind(val value: String) {
    companion object {
        val EP = CardKind("EP") // остаток средств
        val SU = CardKind("SU") // безлимит - срок действия
        val LT = CardKind("LT") // остаток поездок

        fun of(kind: String?) = when (kind) {
            EP.value -> EP
            SU.value -> SU
            LT.value -> LT
            else -> LT
        }
    }
}