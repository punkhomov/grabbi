package punkhomov.grabbi.http

import io.ktor.client.statement.*
import punkhomov.grabbi.core.utils.qualifiedNameOrThrow
import kotlin.reflect.KClass

/**
 * The container is used to store both the original [io.ktor.client.statement.HttpResponse]
 * and its transformed versions.
 *
 * @property httpResponse The original [io.ktor.client.statement.HttpResponse].
 */
class TransformedResponse(val httpResponse: HttpResponse) {
    private val transformed = HashMap<String, Any>()

    /**
     * The number of transformed versions.
     */
    val size: Int by transformed::size

    /**
     * Puts the transformed version using the corresponding class type as the key.
     *
     * @param kClass The type key.
     * @param value The transformed version of specified type.
     */
    fun <T : Any> putAs(kClass: KClass<T>, value: T) {
        val key = getKey(kClass)
        transformed[key] = value
    }

    /**
     * Gets the transformed version using the corresponding class type as the key.
     *
     * @param kClass The type key.
     * @return The transformed version of specified type.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrNullAs(kClass: KClass<T>): T? {
        val key = getKey(kClass)
        val value = transformed[key]
        return value as T?
    }

    /**
     * Gets the transformed version using the corresponding class type as the key.
     *
     * @param kClass The type key.
     * @return The transformed version of specified type.
     * @throws NoSuchElementException If the transformed version of specified type not found.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrThrowAs(kClass: KClass<T>): T {
        val key = getKey(kClass)
        val value = transformed[key] ?: throw NoSuchElementException("No response is present with '$key' type.")
        return value as T
    }

    private fun getKey(kClass: KClass<*>): String {
        return kClass.qualifiedNameOrThrow()
    }
}


inline fun <reified T : Any> TransformedResponse.getOrNullAs(): T? {
    return getOrNullAs(T::class)
}

inline fun <reified T : Any> TransformedResponse.getOrThrowAs(): T {
    return getOrThrowAs(T::class)
}


inline fun <T : Any> TransformedResponse.tryTransform(
    kClass: KClass<T>,
    block: TransformedResponse.() -> T
) {
    runCatching { block.invoke(this) }.onSuccess { putAs(kClass, it) }
}

inline fun <reified T : Any> TransformedResponse.tryTransform(
    block: TransformedResponse.() -> T
) {
    return this.tryTransform(T::class, block)
}


inline fun <T : Any> TransformedResponse.ifPresentAs(
    kClass: KClass<T>,
    block: TransformedResponse.(data: T) -> Unit
) {
    getOrNullAs(kClass)?.also { block.invoke(this, it) }
}

inline fun <reified T : Any> TransformedResponse.ifPresentAs(
    block: TransformedResponse.(data: T) -> Unit
) {
    ifPresentAs(T::class, block)
}


inline fun <T : Any> TransformedResponse.ifPresentOnlyAs(
    kClass: KClass<T>,
    block: TransformedResponse.(data: T) -> Unit
) {
    getOrNullAs(kClass)?.takeIf { size == 1 }?.also { block.invoke(this, it) }
}

inline fun <reified T : Any> TransformedResponse.ifPresentOnlyAs(
    block: TransformedResponse.(data: T) -> Unit
) {
    ifPresentOnlyAs(T::class, block)
}
