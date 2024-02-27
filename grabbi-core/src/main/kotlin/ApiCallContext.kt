package punkhomov.grabbi.core

import java.util.concurrent.ConcurrentHashMap

/**
 * Represents the context for executing a call. It can be used only once to execute a call,
 * as well as multiple times to access the bound client, service, and call for
 * implementing internal logic. The properties remain mutable throughout the entire
 * lifecycle of the context.
 */
interface ApiCallContext {
    /**
     * The client that created this context.
     */
    val client: ApiClient

    /**
     * The service that built this context.
     */
    val service: ApiService

    /**
     * The call for which this context is intended to execute.
     */
    val call: ApiCall<*>

    /**
     * Sets a property with the specified key and value in the context.
     *
     * @param key The typed key representing the property.
     * @param value The value to be associated with the key.
     */
    fun <T : Any> setProp(key: TypedKey<T>, value: T)

    /**
     * Gets the value associated with the specified key, or throws the
     * `NoSuchElementException` if the property is not set.
     *
     * @param key The typed key representing the property.
     * @return The value associated with the key.
     * @throws NoSuchElementException If the property is not set.
     */
    fun <T : Any> getPropOrThrow(key: TypedKey<T>): T

    /**
     * Gets the value associated with the specified key, or null if the property is not
     * set.
     *
     * @param key The typed key representing the property.
     * @return The value associated with the key, or null if the property is not set.
     */
    fun <T : Any> getPropOrNull(key: TypedKey<T>): T?
}


/**
 * Throws an exception to prematurely terminate the execution of the call, resulting in
 * an [ApiCallResult.Failure] with a specified error cause. It can be called at any stage
 * of the call execution.
 *
 * @param error The exception indicating the error.
 * @throws ApiErrorExceptionWrapper The exception indicating an error.
 */
fun ApiCallContext.respondError(error: Exception): Nothing {
    throw ApiErrorExceptionWrapper(error)
}

/**
 * An exception wrapper class that wraps another exception. Normally it should be caught
 * by the client when executing calls. Used to prematurely terminate the execution of a
 * call with a specific error.
 *
 * @param error The wrapped exception.
 * @property error The wrapped exception.
 * @constructor Creates an instance of ApiErrorExceptionWrapper with the specified
 * wrapped exception.
 */
class ApiErrorExceptionWrapper(val error: Exception) : Exception(
    "It's only a wrapper of exceptions. Normally it should be caught by the client when executing requests.",
    error,
)


class ApiCallContextImpl(
    override val client: ApiClient,
    override val service: ApiService,
    override val call: ApiCall<*>
) : ApiCallContext {
    private val props = ConcurrentHashMap<String, Any>()

    override fun <T : Any> setProp(key: TypedKey<T>, value: T) {
        props[key.key] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getPropOrThrow(key: TypedKey<T>): T {
        return props.getValue(key.key) as T
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getPropOrNull(key: TypedKey<T>): T? {
        return props[key.key] as T?
    }
}