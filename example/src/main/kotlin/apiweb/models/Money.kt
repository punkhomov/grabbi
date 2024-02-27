package punkhomov.grabbi.example.apiweb.models

@JvmInline
value class Money(val value: Long) {
    override fun toString(): String {
        return buildString {
            append(value / 100)
            append('.')
            append((value % 100).toString().padStart(2, '0'))
        }
    }

    companion object {
        fun parse(balance: String): Money {
            val dotIndex = balance.indexOf('.')
            val value = if (dotIndex == -1) {
                balance.toLong() * 100
            } else {
                val rublesPart = balance.substring(0, dotIndex)
                val kopecksPart = balance.substring(dotIndex + 1).padEnd(2, '0')
                require(kopecksPart.length < 3) { "Malformed value." }

                rublesPart.toLong() * 100 + kopecksPart.toLong()
            }

            return Money(value)
        }
    }
}