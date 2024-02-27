package punkhomov.grabbi.http

import punkhomov.grabbi.core.ApiCallContext
import punkhomov.grabbi.core.ApiErrorExceptionWrapper

/**
 * Represents the strategy for checking errors in the response.
 */
fun interface ApiErrorChecker {
    /**
     * Performs error checking on the given response. **Implementations should** collect
     * errors using the [context.respondError(...)][punkhomov.grabbi.core.respondError]
     * method.
     *
     * @param context The context of the call.
     * @param response The response to be checked.
     * @throws ApiErrorExceptionWrapper If this error checker detects an error.
     */
    fun check(context: ApiCallContext, response: TransformedResponse)
}

/**
 * Represents a chain of error checkers to be applied sequentially.
 */
class ErrorCheckerChain {
    private val chain = ArrayList<ApiErrorChecker>()

    /**
     * Appends an error checker to the chain.
     *
     * @param checker The error checker to append.
     * @throws IllegalArgumentException If the same checker is appended multiple times.
     */
    fun append(checker: ApiErrorChecker) {
        require(checker !in chain) {
            "Appending the same checker multiple times is not allowed. The '$checker' is already present in the chain."
        }

        chain.add(checker)
    }

    /**
     * Cancels an error checker previously appended to the chain.
     *
     * @param checker The error checker to cancel.
     * @throws IllegalArgumentException If the specified checker is not present in the chain.
     */
    fun cancel(checker: ApiErrorChecker) {
        require(checker in chain) {
            "Nothing to cancel. The '$checker' is not present in the chain."
        }

        chain.remove(checker)
    }

    /**
     * Checks if a specific error checker is present in the chain.
     *
     * @param checker The error checker to check.
     * @return `true` if the checker is present, `false` otherwise.
     *
     */
    operator fun contains(checker: ApiErrorChecker): Boolean {
        return chain.contains(checker)
    }

    /**
     * Processes the entire chain of checkers by invoking the
     * [check][ApiErrorChecker.check] method of each checker in the order they were added.
     *
     * @param context The context of the call.
     * @param response The response to be checked.
     * @throws ApiErrorExceptionWrapper If any error checker detects an error.
     */
    fun process(context: ApiCallContext, response: TransformedResponse) {
        chain.forEach {
            it.check(context, response)
        }
    }
}