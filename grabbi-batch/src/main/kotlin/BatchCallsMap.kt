package punkhomov.grabbi.batch

import punkhomov.grabbi.core.ApiCall
import punkhomov.grabbi.core.TypedKey

typealias CallFactory<TContext, TResult> = TContext.() -> ApiCall<TResult>

typealias BatchCallsMapWithUnit = BatchCallsMap<Unit>
typealias BatchCallsMapWithResponses = BatchCallsMap<BatchCallResponses.ReadAccess>

data class QualifiedCall<T>(
    val qualifier: TypedKey<T>,
    val call: ApiCall<T>
)

class BatchCallsMap<TContext>(private val receiver: TContext) : Iterable<QualifiedCall<Any?>> {
    private val calls = LinkedHashMap<String, CallFactory<TContext, *>>()

    fun <T> call(qualifier: TypedKey<T>, callFactory: CallFactory<TContext, T>) {
        calls[qualifier.key] = callFactory
    }

    override fun iterator(): Iterator<QualifiedCall<Any?>> {
        return IteratorImpl()
    }

    inner class IteratorImpl : Iterator<QualifiedCall<Any?>> {
        private val it = calls.iterator()

        override fun hasNext(): Boolean {
            return it.hasNext()
        }

        @Suppress("UNCHECKED_CAST")
        override fun next(): QualifiedCall<Any?> {
            val next = it.next()

            val qualifier = TypedKey<Any?>(next.key)
            val call = next.value.invoke(receiver) as ApiCall<Any?>

            return QualifiedCall(qualifier, call)
        }
    }
}