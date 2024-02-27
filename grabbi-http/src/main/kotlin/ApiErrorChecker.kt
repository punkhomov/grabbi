package punkhomov.grabbi.http

import punkhomov.grabbi.core.ApiCallContext

fun interface ApiErrorChecker {
    fun check(context: ApiCallContext, response: TransformedResponse)
}

class ErrorCheckerChain {
    private val chain = ArrayList<ApiErrorChecker>()

    fun append(checker: ApiErrorChecker) {
        if (checker !in chain) {
            chain.add(checker)
        } else {
            error("Appending the same checker multiple times is not allowed. The '$checker' is already present in the chain.")
        }
    }

    fun cancel(checker: ApiErrorChecker) {
        if (checker in chain) {
            chain.remove(checker)
        } else {
            error("Nothing to cancel. The '$checker' is not present in the chain.")
        }
    }

    operator fun contains(checker: ApiErrorChecker): Boolean {
        return chain.contains(checker)
    }

    fun process(context: ApiCallContext, response: TransformedResponse) {
        chain.forEach {
            it.check(context, response)
        }
    }
}