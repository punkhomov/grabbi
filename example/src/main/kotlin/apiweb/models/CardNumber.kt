package punkhomov.grabbi.example.apiweb.models

@JvmInline
value class CardNumber private constructor(val value: String) {
    enum class Format {
        CN_19, CN_9
    }

    val format: Format
        get() = when (value.length) {
            19 -> Format.CN_19
            9 -> Format.CN_9
            else -> throw UnsupportedOperationException()
        }

    val isValid: Boolean
        get() = when (format) {
            Format.CN_19 -> cn19VerifyCheckDigit(value)
            Format.CN_9 -> true
        }

    val prettyNumber: String
        get() = when (format) {
            Format.CN_19 -> prettyCn19(value)
            Format.CN_9 -> prettyCn9(value)
        }

    companion object {
        fun from(number: String): CardNumber = when (number.length) {
            19 -> CardNumber(number)
            9 -> CardNumber(number)
            else -> error("Malformed card number.")
        }

        private fun cn19VerifyCheckDigit(number: String): Boolean {
            val sum = cn19CalcCheckDigitSum(number)

            return sum % 10 == 0
        }

        private fun cn19CalcCheckDigitSum(number: String): Int {
            return number.reversed().foldIndexed(0) { index, acc, char ->
                val k = if (index % 2 == 1) 2 else 1
                var res = char.digitToInt() * k
                if (res > 9) {
                    res -= 9
                }
                acc + res
            }
        }

        private fun prettyCn19(value: String): String {
            // 9643 10630 00000 00000
            return StringBuilder(value).apply {
                insert(4, ' ')
                insert(10, ' ')
                insert(16, ' ')
            }.toString()
        }

        private fun prettyCn9(value: String): String {
            // 100 000 000
            return StringBuilder(value).apply {
                insert(3, ' ')
                insert(7, ' ')
            }.toString()
        }
    }
}