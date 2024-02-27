package punkhomov.grabbi.batch

import punkhomov.grabbi.core.TypedKey
import java.util.concurrent.ConcurrentHashMap

class BatchCallResponses {
    private val responses = ConcurrentHashMap<String, Any>()

    val writer = WriteAccess()
    val reader = ReadAccess()

    inner class WriteAccess {
        fun <T> putResponse(qualifier: TypedKey<T>, response: T) {
            check(responses.containsKey(qualifier.key).not()) {
                "The response cannot be reassigned with qualifier '${qualifier.key}'"
            }

            responses[qualifier.key] = response ?: NULL
        }
    }

    inner class ReadAccess {
        @Suppress("UNCHECKED_CAST")
        fun <T> getResponse(qualifier: TypedKey<T>): T {
            check(responses.containsKey(qualifier.key)) {
                "No response assigned with qualifier '${qualifier.key}'"
            }

            val response = responses.getValue(qualifier.key)
            return if (response == NULL) {
                null as T
            } else {
                response as T
            }

        }
    }

    companion object {
        private val NULL = Any()
    }
}